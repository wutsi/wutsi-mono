package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.workflow.UnpublishProductWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class UnpublishProductDelegate(private val workflow: UnpublishProductWorkflow) {
    public fun invoke(id: Long) {
        workflow.execute(id, WorkflowContext())
    }
}
