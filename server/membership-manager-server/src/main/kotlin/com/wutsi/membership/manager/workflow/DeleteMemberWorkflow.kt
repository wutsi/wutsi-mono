package com.wutsi.membership.manager.workflow

import com.wutsi.membership.manager.workflow.task.DeactivateAccountTask
import com.wutsi.membership.manager.workflow.task.DeactivatePaymentMethod
import com.wutsi.security.manager.SecurityManagerApi
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.Workflow
import com.wutsi.workflow.engine.WorkflowEngine
import com.wutsi.workflow.util.WorkflowIdGenerator
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class DeleteMemberWorkflow(
    private val workflowEngine: WorkflowEngine,
    private val securityManagerApi: SecurityManagerApi,
) : Workflow {
    companion object {
        val ID = WorkflowIdGenerator.generate("marketplace", "delete-member")
    }

    @PostConstruct
    fun init() {
        workflowEngine.register(ID, this)
    }

    override fun execute(context: WorkflowContext) {
        deletePassword()
        deactivateAccount(context)
        deactivatePaymentMethod(context)
    }

    private fun deletePassword() =
        securityManagerApi.deletePassword()

    private fun deactivateAccount(context: WorkflowContext) =
        workflowEngine.executeAsync(DeactivateAccountTask.ID, context)

    private fun deactivatePaymentMethod(context: WorkflowContext) =
        workflowEngine.executeAsync(DeactivatePaymentMethod.ID, context)
}
