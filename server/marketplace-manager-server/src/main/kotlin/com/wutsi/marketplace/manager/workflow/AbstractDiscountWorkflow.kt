package com.wutsi.marketplace.manager.workflow

import com.wutsi.event.ProductEventPayload
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.Rule
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.rule.account.AccountShouldBeActiveRule
import com.wutsi.workflow.rule.account.AccountShouldBeBusinessRule
import com.wutsi.workflow.rule.account.AccountShouldBeOwnerOfStoreRule
import com.wutsi.workflow.rule.account.AccountShouldHaveStoreRule
import com.wutsi.workflow.rule.account.StoreShouldBeActiveRule

abstract class AbstractDiscountWorkflow<Req, Resp>(eventStream: EventStream) :
    AbstractMarketplaceWorkflow<Req, Resp, ProductEventPayload>(eventStream) {
    override fun getEventType(request: Req, response: Resp, context: WorkflowContext): String? = null
    override fun toEventPayload(request: Req, response: Resp, context: WorkflowContext): ProductEventPayload? = null

    override fun getValidationRules(request: Req, context: WorkflowContext): RuleSet {
        val account = getCurrentAccount(context)
        val store = account.storeId?.let {
            getCurrentStore(account, context)
        }

        val rules = mutableListOf(
            AccountShouldBeBusinessRule(account),
            AccountShouldBeActiveRule(account),
            AccountShouldHaveStoreRule(account),
            store?.let { AccountShouldBeOwnerOfStoreRule(account, it) },
            store?.let { StoreShouldBeActiveRule(it) },
        )
        rules.addAll(getAdditionalRules(request))
        return RuleSet(
            rules.filterNotNull(),
        )
    }

    protected open fun getAdditionalRules(request: Req): List<Rule?> = emptyList()
}
