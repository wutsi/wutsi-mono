package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.util.WorkflowIdGenerator
import org.springframework.stereotype.Service

@Service
class GetMemberByNameWorkflow : AbstractGetMemberWorkflow() {
    companion object {
        val ID = WorkflowIdGenerator.generate("membership-manager", "get-member-by-name")
    }

    override fun id(): String = ID

    override fun findAccount(context: WorkflowContext, membershipAccessApi: MembershipAccessApi): Account {
        val name = context.input.toString()
        return membershipAccessApi.getAccountByName(name).account
    }
}
