package com.wutsi.checkout.manager.delegate

import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.access.dto.Business
import com.wutsi.checkout.access.dto.PaymentMethod
import com.wutsi.checkout.manager.dto.CreateDonationRequest
import com.wutsi.checkout.manager.dto.CreateDonationResponse
import com.wutsi.checkout.manager.event.EventHander
import com.wutsi.checkout.manager.event.NotifyDonationEventPayload
import com.wutsi.checkout.manager.event.TransactionEventPayload
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.payment.core.Status
import com.wutsi.regulation.RuleSet
import com.wutsi.regulation.rule.AccountShouldBeActiveRule
import com.wutsi.regulation.rule.BusinessShouldBeActive
import com.wutsi.regulation.rule.PaymentMethodShouldBeActive
import feign.FeignException
import org.springframework.stereotype.Service

@Service
public class CreateDonationDelegate(
    private val logger: KVLogger,
    private val checkoutAccessApi: CheckoutAccessApi,
    private val membershipAccessApi: MembershipAccessApi,
    private val eventStream: EventStream,
) : AbstractTransactionDelegate() {
    override fun handleSuccess(transactionId: String) {
        eventStream.enqueue(EventHander.EVENT_NOTIFY_DONATION_TO_MERCHANT, NotifyDonationEventPayload(transactionId))
    }

    fun invoke(request: CreateDonationRequest): CreateDonationResponse {
        logger.add("request_business_id", request.businessId)
        logger.add("request_amount", request.amount)
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
        validate(account, business, paymentMethod)

        val response = donate(business, request)
        if (response.status == Status.SUCCESSFUL.name) {
            eventStream.enqueue(EventHander.EVENT_HANDLE_SUCCESSFUL_TRANSACTION,
                TransactionEventPayload(response.transactionId))
        }

        return response
    }

    private fun validate(account: Account, business: Business, paymentMethod: PaymentMethod?) =
        RuleSet(
            listOfNotNull(
                AccountShouldBeActiveRule(account),
                BusinessShouldBeActive(business),
                paymentMethod?.let { PaymentMethodShouldBeActive(it) },
            ),
        ).check()

    private fun donate(business: Business, request: CreateDonationRequest): CreateDonationResponse {
        val response = createTransaction(request, business)
        logger.add("transaction_id", response.transactionId)
        logger.add("transaction_status", response.status)

        return CreateDonationResponse(
            transactionId = response.transactionId,
            status = response.status,
        )
    }

    private fun createTransaction(
        request: CreateDonationRequest,
        business: Business,
    ): com.wutsi.checkout.access.dto.CreateDonationResponse {
        try {
            return checkoutAccessApi.createDonation(
                request = com.wutsi.checkout.access.dto.CreateDonationRequest(
                    email = request.email,
                    paymentMethodToken = request.paymentMethodToken,
                    paymentMethodType = request.paymentMethodType,
                    paymentProviderId = request.paymentProviderId,
                    paymentMethodOwnerName = request.paymentMethodOwnerName,
                    paymenMethodNumber = request.paymenMethodNumber,
                    businessId = business.id,
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
