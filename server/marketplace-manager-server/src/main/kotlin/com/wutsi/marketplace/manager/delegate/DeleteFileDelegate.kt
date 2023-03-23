package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.workflow.DeleteFileWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class DeleteFileDelegate(private val workflow: DeleteFileWorkflow) {
    public fun invoke(id: Long) {
        workflow.execute(id, WorkflowContext())
    }
}
