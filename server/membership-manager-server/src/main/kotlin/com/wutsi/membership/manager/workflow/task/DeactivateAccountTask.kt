package com.wutsi.membership.manager.workflow.task

import com.wutsi.enums.AccountStatus
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.UpdateAccountStatusRequest
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.Workflow
import com.wutsi.workflow.engine.WorkflowEngine
import com.wutsi.workflow.util.WorkflowIdGenerator
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class DeactivateAccountTask(
    private val workflowEngine: WorkflowEngine,
    private val membershipAccessApi: MembershipAccessApi,
) : Workflow {
    companion object {
        val ID = WorkflowIdGenerator.generate("membership-manager", "deactivate-account")
    }

    @PostConstruct
    fun init() {
        workflowEngine.register(ID, this)
    }

    override fun execute(context: WorkflowContext) {
        membershipAccessApi.updateAccountStatus(
            id = context.accountId!!,
            request = UpdateAccountStatusRequest(
                status = AccountStatus.INACTIVE.name,
            ),
        )
    }
}
