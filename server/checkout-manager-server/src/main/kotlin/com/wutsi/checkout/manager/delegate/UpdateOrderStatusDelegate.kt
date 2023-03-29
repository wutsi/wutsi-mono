package com.wutsi.checkout.manager.delegate

import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.access.dto.Order
import com.wutsi.checkout.manager.dto.UpdateOrderStatusRequest
import com.wutsi.checkout.manager.event.CancelReservationEventPayload
import com.wutsi.checkout.manager.event.EventHander.Companion.EVENT_CANCEL_RESERVATION
import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.enums.OrderStatus
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.regulation.RuleSet
import com.wutsi.regulation.rule.AccountShouldBeOwnerOfOrder
import org.springframework.stereotype.Service

@Service
class UpdateOrderStatusDelegate(
    private val checkoutAccessApi: CheckoutAccessApi,
    private val membershipAccessApi: MembershipAccessApi,
    private val eventStream: EventStream,
) {
    fun invoke(request: UpdateOrderStatusRequest) {
        invoke(request, SecurityUtil.getAccountId())
    }

    fun invoke(request: UpdateOrderStatusRequest, accountId: Long?) {
        val order = checkoutAccessApi.getOrder(request.orderId).order
        if (order.status == request.status) {
            return
        }

        val account = accountId?.let { membershipAccessApi.getAccount(accountId).account }
        validate(account, order)

        updateStatus(request)
        when (request.status.uppercase()) {
            OrderStatus.CANCELLED.name,
            OrderStatus.EXPIRED.name,
            -> cancelReservation(order.id)
        }
    }

    private fun validate(account: Account?, order: Order) =
        RuleSet(
            rules = listOfNotNull(
                account?.let { AccountShouldBeOwnerOfOrder(account, order) },
            ),
        ).check()

    private fun updateStatus(request: UpdateOrderStatusRequest) =
        checkoutAccessApi.updateOrderStatus(
            id = request.orderId,
            request = com.wutsi.checkout.access.dto.UpdateOrderStatusRequest(
                status = request.status,
                reason = request.reason,
            ),
        )

    private fun cancelReservation(orderId: String) =
        eventStream.enqueue(EVENT_CANCEL_RESERVATION, CancelReservationEventPayload(orderId))
}
