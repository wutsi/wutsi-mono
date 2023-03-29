package com.wutsi.checkout.manager.delegate

import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.access.dto.Business
import com.wutsi.checkout.access.dto.PaymentMethod
import com.wutsi.checkout.manager.dto.CreateCashoutRequest
import com.wutsi.checkout.manager.dto.CreateCashoutResponse
import com.wutsi.checkout.manager.event.EventHander
import com.wutsi.checkout.manager.event.TransactionEventPayload
import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.payment.core.Status
import com.wutsi.regulation.RuleSet
import com.wutsi.regulation.rule.AccountShouldBeActiveRule
import com.wutsi.regulation.rule.AccountShouldBeBusinessRule
import com.wutsi.regulation.rule.BusinessShouldBeActive
import com.wutsi.regulation.rule.PaymentMethodShouldBeActive
import feign.FeignException
import org.springframework.stereotype.Service

@Service
public class CreateCashoutDelegate(
    private val checkoutAccessApi: CheckoutAccessApi,
    private val membershipAccessApi: MembershipAccessApi,
    private val logger: KVLogger,
    private val eventStream: EventStream,
) : AbstractTransactionDelegate() {
    fun invoke(request: CreateCashoutRequest): CreateCashoutResponse {
        logger.add("request_payment_token", request.paymentMethodToken)
        logger.add("request_description", request.description)
        logger.add("request_idempotency_key", request.idempotencyKey)
        logger.add("request_amount", request.amount)

        val account = membershipAccessApi.getAccount(SecurityUtil.getAccountId()).account
        val business = account.businessId?.let { checkoutAccessApi.getBusiness(it).business }
        val paymentMethod = checkoutAccessApi.getPaymentMethod(request.paymentMethodToken).paymentMethod

        validate(account, business, paymentMethod)

        val response = cashOut(account, request)
        if (response.status == Status.SUCCESSFUL.name) {
            eventStream.enqueue(EventHander.EVENT_HANDLE_SUCCESSFUL_TRANSACTION,
                TransactionEventPayload(response.transactionId))
        }

        return response
    }

    private fun validate(account: Account, business: Business?, paymentMethod: PaymentMethod) =
        RuleSet(
            listOfNotNull(
                AccountShouldBeActiveRule(account),
                AccountShouldBeBusinessRule(account),
                business?.let { BusinessShouldBeActive(it) },
                PaymentMethodShouldBeActive(paymentMethod),
            ),
        ).check()

    private fun cashOut(account: Account, request: CreateCashoutRequest): CreateCashoutResponse {
        val response = createTransaction(account, request)
        logger.add("transaction_id", response.transactionId)
        logger.add("transaction_status", response.status)

        return CreateCashoutResponse(
            transactionId = response.transactionId,
            status = response.status,
        )
    }

    private fun createTransaction(
        account: Account,
        request: CreateCashoutRequest,
    ): com.wutsi.checkout.access.dto.CreateCashoutResponse {
        try {
            return checkoutAccessApi.createCashout(
                request = com.wutsi.checkout.access.dto.CreateCashoutRequest(
                    email = account.email ?: "",
                    businessId = account.businessId!!,
                    paymentMethodToken = request.paymentMethodToken,
                    idempotencyKey = request.idempotencyKey,
                    amount = request.amount,
                    description = request.description,
                ),
            )
        } catch (ex: FeignException) {
            throw handleTransactionException(ex)
        }
    }
}
