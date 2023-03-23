package com.wutsi.marketplace.manager.workflow

import com.wutsi.marketplace.manager.dto.UpdateProductAttributeListRequest
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class UpdateProductAttributeWorkflow(
    eventStream: EventStream,
) : AbstractProductWorkflow<UpdateProductAttributeListRequest, Unit>(eventStream) {
    override fun getProductId(request: UpdateProductAttributeListRequest, context: WorkflowContext): Long? =
        request.productId

    override fun doExecute(request: UpdateProductAttributeListRequest, context: WorkflowContext) {
        request.attributes.forEach {
            marketplaceAccessApi.updateProductAttribute(
                id = request.productId,
                request = com.wutsi.marketplace.access.dto.UpdateProductAttributeRequest(
                    name = it.name,
                    value = it.value,
                ),
            )
        }
    }
}
