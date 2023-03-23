package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.workflow.DeletePictureWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class DeletePictureDelegate(private val workflow: DeletePictureWorkflow) {
    fun invoke(id: Long) {
        workflow.execute(id, WorkflowContext())
    }
}
