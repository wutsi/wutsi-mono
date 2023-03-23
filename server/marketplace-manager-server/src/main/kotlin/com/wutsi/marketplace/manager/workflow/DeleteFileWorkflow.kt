package com.wutsi.marketplace.manager.workflow

import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class DeleteFileWorkflow(
    eventStream: EventStream,
) : AbstractProductWorkflow<Long, Unit>(eventStream) {
    override fun getProductId(fileId: Long, context: WorkflowContext): Long? =
        null

    override fun doExecute(fileId: Long, context: WorkflowContext) {
        marketplaceAccessApi.deleteFile(fileId)
    }
}
