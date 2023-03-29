package com.wutsi.checkout.manager.delegate

import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.access.dto.CreatePaymentMethodRequest
import com.wutsi.checkout.manager.dto.AddPaymentMethodRequest
import com.wutsi.checkout.manager.dto.AddPaymentMethodResponse
import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.rule.account.AccountShouldBeActiveRule
import org.springframework.stereotype.Service

@Service
class AddPaymentMethodDelegate(
    private val checkoutAccessApi: CheckoutAccessApi,
    private val membershipAccessApi: MembershipAccessApi,
    private val logger: KVLogger,
) {
    fun invoke(request: AddPaymentMethodRequest): AddPaymentMethodResponse {
        logger.add("request_provider_id", request.providerId)
        logger.add("request_country", request.country)
        logger.add("request_number", "...." + request.number.takeLast(4))
        logger.add("request_type", request.type)
        logger.add("request_owner_name", request.ownerName)

        val account = membershipAccessApi.getAccount(SecurityUtil.getAccountId()).account
        validate(account)
        val token = add(account.id, request)

        return AddPaymentMethodResponse(paymentMethodToken = token)
    }

    private fun validate(account: Account) =
        RuleSet(
            listOf(
                AccountShouldBeActiveRule(account),
            ),
        ).check()

    private fun add(accountId: Long, request: AddPaymentMethodRequest): String =
        checkoutAccessApi.createPaymentMethod(
            request = CreatePaymentMethodRequest(
                accountId = accountId,
                type = request.type,
                number = request.number,
                country = request.country,
                ownerName = request.ownerName,
                providerId = request.providerId,
            ),
        ).paymentMethodToken
}
