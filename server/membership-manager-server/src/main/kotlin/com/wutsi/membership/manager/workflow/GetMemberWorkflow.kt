package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.util.WorkflowIdGenerator
import org.springframework.stereotype.Service

@Service
class GetMemberWorkflow : AbstractGetMemberWorkflow() {
    companion object {
        val ID = WorkflowIdGenerator.generate("membership-manager", "get-member")
    }

    override fun id(): String = ID

    override fun findAccount(context: WorkflowContext, membershipAccessApi: MembershipAccessApi): Account {
        return membershipAccessApi.getAccount(context.accountId!!).account
    }
}
