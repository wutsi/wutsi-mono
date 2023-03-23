package com.wutsi.marketplace.manager.workflow

import com.wutsi.marketplace.manager.dto.UpdateStorePolicyAttributeRequest
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class UpdateStorePolicyAttributeWorkflow(
    eventStream: EventStream,
) : AbstractDiscountWorkflow<UpdateStorePolicyAttributeRequest, Unit>(eventStream) {
    companion object {
        const val ID = "id"
    }

    override fun doExecute(
        request: UpdateStorePolicyAttributeRequest,
        context: WorkflowContext,
    ) {
        marketplaceAccessApi.updateStorePolicyAttribute(
            context.data[ID] as Long,
            com.wutsi.marketplace.access.dto.UpdateStorePolicyAttributeRequest(
                name = request.name,
                value = request.value,
            ),
        )
    }
}
