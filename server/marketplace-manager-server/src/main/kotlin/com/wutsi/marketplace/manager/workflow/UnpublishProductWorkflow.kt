package com.wutsi.marketplace.manager.workflow

import com.wutsi.enums.ProductStatus
import com.wutsi.marketplace.access.dto.UpdateProductStatusRequest
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class UnpublishProductWorkflow(
    eventStream: EventStream,
) : AbstractProductWorkflow<Long, Unit>(eventStream) {
    override fun getProductId(productId: Long, context: WorkflowContext): Long? =
        productId

    override fun doExecute(productId: Long, context: WorkflowContext) {
        marketplaceAccessApi.updateProductStatus(
            id = productId,
            request = UpdateProductStatusRequest(
                status = ProductStatus.DRAFT.name,
            ),
        )
    }
}
