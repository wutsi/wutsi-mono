package com.wutsi.membership.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.access.dto.SearchPaymentMethodRequest
import com.wutsi.checkout.access.dto.UpdatePaymentMethodStatusRequest
import com.wutsi.enums.AccountStatus
import com.wutsi.enums.PaymentMethodStatus
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.UpdateAccountStatusRequest
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.security.manager.SecurityManagerApi
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class DeleteMemberDelegate(
    private val membershipAccessApi: MembershipAccessApi,
    private val securityManagerApi: SecurityManagerApi,
    private val checkoutAccessApi: CheckoutAccessApi,
    private val eventStream: EventStream,
    private val objectMapper: ObjectMapper,
    private val logger: KVLogger,
) {
    companion object {
        const val DEACTIVATE_ACCOUNT = "urn:wutsi:event:membership-manager:deactivate-account"
        const val DEACTIVATE_PAYMENT_METHOD = "urn:wutsi:event:membership-manager:deactivate-payment-method"
    }

    fun invoke() {
        val accountId = SecurityUtil.getAccountId()
        logger.add("account_id", accountId)

        deletePassword()
        deactivateAccount(accountId)
        deactivatePaymentMethod(accountId)
    }

    @EventListener
    fun onEvent(event: Event) {
        when (event.type) {
            DEACTIVATE_ACCOUNT -> onDeactivateAccount(toAccountId(event))
            DEACTIVATE_PAYMENT_METHOD -> onDeactivatePaymentMethod(toAccountId(event))
            else -> {}
        }
    }

    private fun toAccountId(event: Event): Long =
        objectMapper.readValue(event.payload, DeleteMemberEventPayload::class.java).accountId

    private fun deletePassword() {
        securityManagerApi.deletePassword()
    }

    private fun deactivateAccount(accountId: Long) {
        eventStream.enqueue(DEACTIVATE_ACCOUNT, DeleteMemberEventPayload(accountId))
    }

    private fun onDeactivateAccount(accountId: Long) {
        membershipAccessApi.updateAccountStatus(
            id = accountId,
            request = UpdateAccountStatusRequest(
                status = AccountStatus.INACTIVE.name,
            ),
        )
    }

    private fun deactivatePaymentMethod(accountId: Long) {
        eventStream.enqueue(DEACTIVATE_PAYMENT_METHOD, DeleteMemberEventPayload(accountId))
    }

    private fun onDeactivatePaymentMethod(accountId: Long) {
        checkoutAccessApi.searchPaymentMethod(
            request = SearchPaymentMethodRequest(
                accountId = accountId,
                status = PaymentMethodStatus.ACTIVE.name,
            ),
        ).paymentMethods.forEach {
            checkoutAccessApi.updatePaymentMethodStatus(
                token = it.token,
                request = UpdatePaymentMethodStatusRequest(
                    status = PaymentMethodStatus.INACTIVE.name,
                ),
            )
        }
    }
}

data class DeleteMemberEventPayload(val accountId: Long = -1)
