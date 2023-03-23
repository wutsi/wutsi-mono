package com.wutsi.marketplace.manager.workflow

import com.wutsi.event.StoreEventPayload
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.Rule
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext

abstract class AbstractStoreWorkflow<Req, Resp>(eventStream: EventStream) :
    AbstractMarketplaceWorkflow<Req, Resp, StoreEventPayload>(eventStream) {
    override fun getValidationRules(request: Req, context: WorkflowContext): RuleSet {
        val account = getCurrentAccount(context)
        return RuleSet(
            getAdditionalRules(account, context).filterNotNull(),
        )
    }

    protected open fun getAdditionalRules(account: Account, ctx: WorkflowContext): List<Rule?> = emptyList()
}
