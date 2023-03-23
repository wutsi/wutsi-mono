package com.wutsi.marketplace.manager.workflow

import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.access.dto.CreateStoreRequest
import com.wutsi.marketplace.access.dto.CreateStoreResponse
import com.wutsi.marketplace.manager.workflow.task.SetAccountStoreTask
import com.wutsi.marketplace.manager.workflow.task.WelcomeEmailTask
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.regulation.RegulationEngine
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.Workflow
import com.wutsi.workflow.engine.WorkflowEngine
import com.wutsi.workflow.rule.account.AccountShouldBeActiveRule
import com.wutsi.workflow.rule.account.AccountShouldBeBusinessRule
import com.wutsi.workflow.rule.account.CountryShouldSupportStoreRule
import com.wutsi.workflow.util.WorkflowIdGenerator
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class CreateStoreWorkflow(
    private val workflowEngine: WorkflowEngine,
    private val regulationEngine: RegulationEngine,
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val membershipAccessApi: MembershipAccessApi,
    private val logger: KVLogger,
) : Workflow {
    companion object {
        val ID = WorkflowIdGenerator.generate("marketplace", "create-store")
    }

    @PostConstruct
    fun init() {
        workflowEngine.register(ID, this)
    }

    override fun execute(context: WorkflowContext) {
        val account = getCurrentAccount(context)
        logger.add("account_store_id", account.storeId)
        if (account.storeId != null) {
            return
        }

        validate(account)

        // Create the store
        val response = createStore(account)
        logger.add("store_id", response.storeId)

        // Set the account store
        setAccountStore(response.storeId, context)

        // Send welcome email to customer
        sendWelcomeEmail(response.storeId, context)
    }

    private fun getCurrentAccount(context: WorkflowContext): Account =
        membershipAccessApi.getAccount(context.accountId!!).account

    private fun validate(account: Account) {
        val rules = RuleSet(
            listOf(
                AccountShouldBeActiveRule(account),
                AccountShouldBeBusinessRule(account),
                CountryShouldSupportStoreRule(account, regulationEngine),
            ),
        )
        rules.check()
    }

    private fun createStore(account: Account): CreateStoreResponse =
        marketplaceAccessApi.createStore(
            request = CreateStoreRequest(
                accountId = account.id,
                businessId = account.businessId!!,
                currency = regulationEngine.country(account.country).currency,
            ),
        )

    private fun setAccountStore(storeId: Long, context: WorkflowContext) =
        workflowEngine.executeAsync(
            id = SetAccountStoreTask.ID,
            context = WorkflowContext(
                accountId = context.accountId,
                data = mutableMapOf(
                    SetAccountStoreTask.CONTEXT_STORE_ID to storeId,
                ),
            ),
        )

    private fun sendWelcomeEmail(storeId: Long, context: WorkflowContext) =
        workflowEngine.executeAsync(
            id = WelcomeEmailTask.ID,
            context = WorkflowContext(
                accountId = context.accountId,
                data = mutableMapOf(
                    WelcomeEmailTask.CONTEXT_STORE_ID to storeId,
                ),
            ),
        )
}
