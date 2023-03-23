package com.wutsi.marketplace.manager.delegate

import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.marketplace.manager.workflow.CreateStoreWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class CreateStoreDelegate(private val workflow: CreateStoreWorkflow) {
    fun invoke() {
        workflow.execute(
            context = WorkflowContext(accountId = SecurityUtil.getAccountId()),
        )
    }
}
