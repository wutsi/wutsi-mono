package com.wutsi.marketplace.manager.workflow

import com.wutsi.enums.StoreStatus
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.access.dto.UpdateStoreStatusRequest
import com.wutsi.marketplace.manager.workflow.task.SetAccountStoreTask
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.Workflow
import com.wutsi.workflow.engine.WorkflowEngine
import com.wutsi.workflow.util.WorkflowIdGenerator
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class DeactivateStoreWorkflow(
    private val workflowEngine: WorkflowEngine,
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val membershipAccessApi: MembershipAccessApi,
) : Workflow {
    companion object {
        val ID = WorkflowIdGenerator.generate("marketplace", "deactivate-store")
    }

    @PostConstruct
    fun init() {
        workflowEngine.register(ID, this)
    }

    override fun execute(context: WorkflowContext) {
        val account = getCurrentAccount(context)

        // Deactivate the store
        deactivateStore(account)

        // Set the account store
        resetAccountStore(account)
    }

    private fun getCurrentAccount(context: WorkflowContext): Account =
        membershipAccessApi.getAccount(context.accountId!!).account

    private fun deactivateStore(account: Account) =
        account.storeId?.let {
            marketplaceAccessApi.updateStoreStatus(
                id = it,
                request = UpdateStoreStatusRequest(
                    status = StoreStatus.INACTIVE.name,
                ),
            )
        }

    private fun resetAccountStore(account: Account) =
        workflowEngine.executeAsync(
            id = SetAccountStoreTask.ID,
            context = WorkflowContext(
                accountId = account.id,
            ),
        )
}
