package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.workflow.DeleteProductWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class DeleteProductDelegate(private val workflow: DeleteProductWorkflow) {
    public fun invoke(id: Long) {
        workflow.execute(id, WorkflowContext())
    }
}
