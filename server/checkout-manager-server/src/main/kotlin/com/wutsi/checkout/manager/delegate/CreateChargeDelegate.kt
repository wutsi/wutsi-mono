package com.wutsi.checkout.manager.delegate

import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.access.dto.Business
import com.wutsi.checkout.access.dto.Order
import com.wutsi.checkout.access.dto.PaymentMethod
import com.wutsi.checkout.access.dto.UpdateOrderStatusRequest
import com.wutsi.checkout.manager.dto.CreateChargeRequest
import com.wutsi.checkout.manager.dto.CreateChargeResponse
import com.wutsi.checkout.manager.event.EventHander
import com.wutsi.checkout.manager.event.FullfilOrderEventPayload
import com.wutsi.checkout.manager.event.NotifyOrderEventPayload
import com.wutsi.enums.OrderStatus
import com.wutsi.enums.ProductType
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.payment.core.Status
import com.wutsi.regulation.RuleSet
import com.wutsi.regulation.rule.AccountShouldBeActiveRule
import com.wutsi.regulation.rule.BusinessShouldBeActive
import com.wutsi.regulation.rule.OrderShouldNotBeExpiredRule
import com.wutsi.regulation.rule.PaymentMethodShouldBeActive
import feign.FeignException
import org.springframework.stereotype.Service

@Service
public class CreateChargeDelegate(
    private val logger: KVLogger,
    private val checkoutAccessApi: CheckoutAccessApi,
    private val membershipAccessApi: MembershipAccessApi,
    private val eventStream: EventStream,
) : AbstractTransactionDelegate() {
    override fun handleSuccess(transactionId: String) {
        val tx = checkoutAccessApi.getTransaction(transactionId).transaction
        val order = tx.orderId?.let { checkoutAccessApi.getOrder(it).order } ?: return
        if (order.status == OrderStatus.IN_PROGRESS.name) { // Already processed
            return
        }

        // Start order
        checkoutAccessApi.updateOrderStatus(
            id = order.id,
            request = UpdateOrderStatusRequest(status = OrderStatus.IN_PROGRESS.name),
        )

        // Fulfill order
        val fulfill = order.items.all {
            ProductType.valueOf(it.productType).numeric
        }
        if (fulfill) {
            eventStream.enqueue(EventHander.EVENT_FULFILL_ORDER, FullfilOrderEventPayload(order.id))
        }

        // Send Notifications
        eventStream.enqueue(EventHander.EVENT_NOTITY_ORDER_TO_CUSTOMER, NotifyOrderEventPayload(order.id))
        eventStream.enqueue(EventHander.EVENT_NOTIFY_ORDER_TO_MERCHANT, NotifyOrderEventPayload(order.id))
    }

    fun invoke(request: CreateChargeRequest): CreateChargeResponse {
        logger.add("request_order_id", request.orderId)
        logger.add("request_business_id", request.businessId)
        logger.add("request_payment_token", request.paymentMethodToken)
        logger.add("request_payment_method_owner_name", request.paymentMethodOwnerName)
        logger.add("request_payment_method_type", request.paymentMethodType)
        logger.add("request_description", request.description)
        logger.add("request_idempotency_key", request.idempotencyKey)
        logger.add("request_customer_email", request.email)

        val business = checkoutAccessApi.getBusiness(request.businessId).business
        val account = membershipAccessApi.getAccount(business.accountId).account
        val paymentMethod = request.paymentMethodToken?.let {
            checkoutAccessApi.getPaymentMethod(it).paymentMethod
        }
        val order = checkoutAccessApi.getOrder(request.orderId).order
        validate(account, business, paymentMethod, order)

        val response = charge(business, order, request)
        if (response.status == Status.SUCCESSFUL.name) {
            handleSuccess(response.transactionId)
        }

        return response
    }

    private fun validate(account: Account, business: Business, paymentMethod: PaymentMethod?, order: Order) =
        RuleSet(
            listOfNotNull(
                AccountShouldBeActiveRule(account),
                BusinessShouldBeActive(business),
                paymentMethod?.let { PaymentMethodShouldBeActive(it) },
                OrderShouldNotBeExpiredRule(order),
            ),
        ).check()

    private fun charge(business: Business, order: Order, request: CreateChargeRequest): CreateChargeResponse {
        val response = createTransaction(request, business, order)
        logger.add("transaction_id", response.transactionId)
        logger.add("transaction_status", response.status)

        return CreateChargeResponse(
            transactionId = response.transactionId,
            status = response.status,
        )
    }

    private fun createTransaction(
        request: CreateChargeRequest,
        business: Business,
        order: Order,
    ): com.wutsi.checkout.access.dto.CreateChargeResponse {
        try {
            return checkoutAccessApi.createCharge(
                request = com.wutsi.checkout.access.dto.CreateChargeRequest(
                    email = request.email,
                    orderId = request.orderId,
                    paymentMethodToken = request.paymentMethodToken,
                    paymentMethodType = request.paymentMethodType,
                    paymentProviderId = request.paymentProviderId,
                    paymentMethodOwnerName = request.paymentMethodOwnerName,
                    paymenMethodNumber = request.paymenMethodNumber,
                    businessId = business.id,
                    idempotencyKey = request.idempotencyKey,
                    amount = order.balance,
                    description = request.description,
                ),
            )
        } catch (ex: FeignException) {
            throw handleTransactionException(ex)
        }
    }
}
