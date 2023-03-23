package com.wutsi.marketplace.manager.delegate

import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.marketplace.manager.workflow.DeactivateStoreWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class DeactivateStoreDelegate(private val workflow: DeactivateStoreWorkflow) {
    fun invoke() {
        workflow.execute(
            context = WorkflowContext(SecurityUtil.getAccountId()),
        )
    }
}
