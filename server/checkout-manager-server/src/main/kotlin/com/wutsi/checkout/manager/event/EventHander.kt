package com.wutsi.checkout.manager.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.access.dto.UpdateOrderStatusRequest
import com.wutsi.checkout.manager.delegate.CreateCashoutDelegate
import com.wutsi.checkout.manager.delegate.CreateChargeDelegate
import com.wutsi.checkout.manager.mail.OrderCustomerNotifier
import com.wutsi.checkout.manager.mail.OrderMerchantNotifier
import com.wutsi.enums.OrderStatus
import com.wutsi.enums.ProductType
import com.wutsi.enums.ReservationStatus
import com.wutsi.enums.TransactionType
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.access.dto.SearchReservationRequest
import com.wutsi.marketplace.access.dto.UpdateReservationStatusRequest
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.payment.core.Status
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
public class EventHander(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val membershipAccessApi: MembershipAccessApi,
    private val checkoutAccessApi: CheckoutAccessApi,
    private val objectMapper: ObjectMapper,
    private val eventStream: EventStream,
    private val orderCustomerNotifier: OrderCustomerNotifier,
    private val orderMerchantNotifier: OrderMerchantNotifier,
    private val chargeDelegate: CreateChargeDelegate,
    private val cashoutDelegate: CreateCashoutDelegate,
) {
    companion object {
        const val EVENT_CANCEL_RESERVATION: String = "urn:event:checkout-manager:cancel-reservation"
        const val EVENT_CHARGE_SUCCESSFUL = "urn:wutsi:event:checkout-manager:charge-successful"
        const val EVENT_FULFILL_ORDER = "urn:wutsi:event:checkout-manager:fulfil-order"
        const val EVENT_NOTIFY_ORDER_TO_MERCHANT = "urn:wutsi:event:checkout-manager:notify-order-to-merchant"
        const val EVENT_NOTITY_ORDER_TO_CUSTOMER = "urn:wutsi:event:checkout-manager:notify-order-to-customer"
        const val EVENT_SET_ACCOUNT_BUSINESS = "urn:wutsi:event:checkout-manager:set-account-business"
        const val EVENT_HANDLE_SUCCESSFUL_TRANSACTION = "urn:wutsi:event:checkout-manager:handle-sucessful-transaction"
    }

    @EventListener
    fun onEvent(event: Event) {
        when (event.type) {
            EVENT_CANCEL_RESERVATION -> doCancelReservation(event)
            EVENT_CHARGE_SUCCESSFUL -> doHandleSuccessfulCharge(event)
            EVENT_FULFILL_ORDER -> doFulfilOrder(event)
            EVENT_NOTIFY_ORDER_TO_MERCHANT -> doNotifyOrderToMerchant(event)
            EVENT_NOTITY_ORDER_TO_CUSTOMER -> doNotifyOrderCustomer(event)
            EVENT_SET_ACCOUNT_BUSINESS -> doSetAccountBusinessId(event)
            EVENT_HANDLE_SUCCESSFUL_TRANSACTION -> doHandleSuccessfulTransaction(event)
        }
    }

    private fun doCancelReservation(event: Event) {
        val payload = objectMapper.readValue(event.payload, CancelReservationEventPayload::class.java)
        marketplaceAccessApi.searchReservation(
            SearchReservationRequest(
                orderId = payload.orderId
            ),
        ).reservations.forEach {
            marketplaceAccessApi.updateReservationStatus(
                id = it.id,
                request = UpdateReservationStatusRequest(
                    status = ReservationStatus.CANCELLED.name,
                ),
            )
        }
    }

    private fun doSetAccountBusinessId(event: Event) {
        val payload = objectMapper.readValue(event.payload, SetAccountBusinessEventPayload::class.java)
        membershipAccessApi.updateAccountAttribute(
            payload.accountId,
            UpdateAccountAttributeRequest("business-id", payload.businessId.toString())
        )
    }

    private fun doHandleSuccessfulCharge(event: Event) {
        val payload = objectMapper.readValue(event.payload, TransactionEventPayload::class.java)
        val tx = checkoutAccessApi.getTransaction(payload.transactionId).transaction
        val order = tx.orderId?.let { checkoutAccessApi.getOrder(it).order } ?: return
        if (order.status == OrderStatus.IN_PROGRESS.name) { // Already processed
            return
        }

        // Start order
        checkoutAccessApi.updateOrderStatus(
            id = order.id,
            request = UpdateOrderStatusRequest(status = OrderStatus.IN_PROGRESS.name)
        )

        // Fulfill order
        val fulfill = order.items.all {
            ProductType.valueOf(it.productType).numeric
        }
        if (fulfill) {
            eventStream.enqueue(EVENT_FULFILL_ORDER, FullfilOrderEventPayload(order.id))
        }

        // Send Notifications
        eventStream.enqueue(EVENT_NOTITY_ORDER_TO_CUSTOMER, MailOrderEventPayload(order.id))
        eventStream.enqueue(EVENT_NOTIFY_ORDER_TO_MERCHANT, MailOrderEventPayload(order.id))
    }

    private fun doNotifyOrderCustomer(event: Event) {
        val payload = objectMapper.readValue(event.payload, MailOrderEventPayload::class.java)
        orderCustomerNotifier.send(payload.orderId)
    }

    private fun doNotifyOrderToMerchant(event: Event) {
        val payload = objectMapper.readValue(event.payload, MailOrderEventPayload::class.java)
        orderMerchantNotifier.send(payload.orderId)
    }

    private fun doFulfilOrder(event: Event) {
        val payload = objectMapper.readValue(event.payload, FullfilOrderEventPayload::class.java)
        checkoutAccessApi.updateOrderStatus(
            id = payload.orderId,
            request = UpdateOrderStatusRequest(status = OrderStatus.COMPLETED.name)
        )
    }

    private fun doHandleSuccessfulTransaction(event: Event) {
        val payload = objectMapper.readValue(event.payload, TransactionEventPayload::class.java)
        val tx = checkoutAccessApi.getTransaction(payload.transactionId).transaction
        if (tx.status == Status.SUCCESSFUL.name) {
            when (tx.type) {
                TransactionType.CHARGE.name -> chargeDelegate.handleSuccess(payload.transactionId)
                TransactionType.CASHOUT.name -> cashoutDelegate.handleSuccess(payload.transactionId)
            }
        }
    }
}

