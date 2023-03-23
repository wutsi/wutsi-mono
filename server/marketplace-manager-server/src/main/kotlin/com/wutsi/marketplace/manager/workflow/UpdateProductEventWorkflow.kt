package com.wutsi.marketplace.manager.workflow

import com.wutsi.marketplace.manager.dto.UpdateProductEventRequest
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class UpdateProductEventWorkflow(
    eventStream: EventStream,
) : AbstractProductWorkflow<UpdateProductEventRequest, Unit>(eventStream) {
    override fun getProductId(request: UpdateProductEventRequest, context: WorkflowContext): Long? =
        request.productId

    override fun doExecute(request: UpdateProductEventRequest, context: WorkflowContext) {
        marketplaceAccessApi.updateProductEvent(
            id = request.productId,
            request = com.wutsi.marketplace.access.dto.UpdateProductEventRequest(
                meetingId = request.meetingId,
                meetingPassword = request.meetingPassword,
                meetingProviderId = request.meetingProviderId,
                starts = request.starts,
                ends = request.ends,
                online = request.online,
            ),
        )
    }
}
