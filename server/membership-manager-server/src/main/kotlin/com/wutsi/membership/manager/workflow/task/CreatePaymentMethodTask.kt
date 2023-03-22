package com.wutsi.membership.manager.workflow.task

import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.access.dto.CreatePaymentMethodRequest
import com.wutsi.checkout.access.dto.SearchPaymentProviderRequest
import com.wutsi.enums.PaymentMethodType
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.Workflow
import com.wutsi.workflow.engine.WorkflowEngine
import com.wutsi.workflow.util.WorkflowIdGenerator
import feign.FeignException
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class CreatePaymentMethodTask(
    private val workflowEngine: WorkflowEngine,
    private val checkoutAccessApi: CheckoutAccessApi,
    private val membershipAccessApi: MembershipAccessApi,
) : Workflow {
    companion object {
        val ID = WorkflowIdGenerator.generate("membership-manager", "create-payment-method")
    }

    @PostConstruct
    fun init() {
        workflowEngine.register(ID, this)
    }

    override fun execute(context: WorkflowContext) {
        val account = membershipAccessApi.getAccount(context.accountId!!).account
        val providers = checkoutAccessApi.searchPaymentProvider(
            SearchPaymentProviderRequest(
                country = account.phone.country,
                number = account.phone.number,
                type = PaymentMethodType.MOBILE_MONEY.name,
            ),
        ).paymentProviders

        if (providers.size == 1) {
            try {
                checkoutAccessApi.createPaymentMethod(
                    request = CreatePaymentMethodRequest(
                        accountId = account.id,
                        type = providers[0].type,
                        number = account.phone.number,
                        country = account.country,
                        ownerName = account.displayName,
                        providerId = providers[0].id,
                    ),
                )
            } catch (ex: FeignException) {
                // Ignore the error
            }
        }
    }
}
