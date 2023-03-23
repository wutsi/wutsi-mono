package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.workflow.PublishProductWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class PublishProductDelegate(private val workflow: PublishProductWorkflow) {
    public fun invoke(id: Long) {
        workflow.execute(id, WorkflowContext())
    }
}
