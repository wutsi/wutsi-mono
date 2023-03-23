package com.wutsi.marketplace.manager.workflow.task

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.Workflow
import com.wutsi.workflow.engine.WorkflowEngine
import com.wutsi.workflow.util.WorkflowIdGenerator
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class SetAccountStoreTask(
    private val workflowEngine: WorkflowEngine,
    private val membershipAccessApi: MembershipAccessApi,
) : Workflow {
    companion object {
        val ID = WorkflowIdGenerator.generate("marketplace", "set-account-store-id")
        const val CONTEXT_STORE_ID = "store-id"
    }

    @PostConstruct
    fun init() {
        workflowEngine.register(ID, this)
    }

    override fun execute(context: WorkflowContext) {
        membershipAccessApi.updateAccountAttribute(
            id = context.accountId!!,
            request = UpdateAccountAttributeRequest(
                name = "store-id",
                value = context.data[CONTEXT_STORE_ID]?.toString(),
            ),
        )
    }
}
