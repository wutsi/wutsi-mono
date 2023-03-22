package com.wutsi.membership.manager.workflow.task

import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.access.dto.SearchPaymentMethodRequest
import com.wutsi.checkout.access.dto.UpdatePaymentMethodStatusRequest
import com.wutsi.enums.PaymentMethodStatus
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.Workflow
import com.wutsi.workflow.engine.WorkflowEngine
import com.wutsi.workflow.util.WorkflowIdGenerator
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class DeactivatePaymentMethod(
    private val workflowEngine: WorkflowEngine,
    private val checkoutAccessApi: CheckoutAccessApi,
) : Workflow {
    companion object {
        val ID = WorkflowIdGenerator.generate("membership-manager", "deactivate-payment-method")
    }

    @PostConstruct
    fun init() {
        workflowEngine.register(ID, this)
    }

    override fun execute(context: WorkflowContext) {
        checkoutAccessApi.searchPaymentMethod(
            request = SearchPaymentMethodRequest(
                accountId = context.accountId!!,
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
