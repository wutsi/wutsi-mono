package com.wutsi.membership.manager.delegate

import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.membership.manager.workflow.DeleteMemberWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class DeleteMemberDelegate(private val workflow: DeleteMemberWorkflow) {
    fun invoke() {
        workflow.execute(
            WorkflowContext(accountId = SecurityUtil.getAccountId()),
        )
    }
}
