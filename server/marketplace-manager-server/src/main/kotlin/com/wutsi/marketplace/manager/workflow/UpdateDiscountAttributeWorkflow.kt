package com.wutsi.marketplace.manager.workflow

import com.wutsi.marketplace.manager.dto.UpdateDiscountAttributeRequest
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class UpdateDiscountAttributeWorkflow(
    eventStream: EventStream,
) : AbstractDiscountWorkflow<UpdateDiscountAttributeRequest, Unit>(eventStream) {
    companion object {
        const val ID = "id"
    }

    override fun doExecute(
        request: UpdateDiscountAttributeRequest,
        context: WorkflowContext,
    ) {
        marketplaceAccessApi.updateDiscountAttribute(
            context.data[ID] as Long,
            com.wutsi.marketplace.access.dto.UpdateDiscountAttributeRequest(
                name = request.name,
                value = request.value,
            ),
        )
    }
}
