package com.wutsi.marketplace.manager.workflow

import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class AddDiscountProductWorkflow(
    eventStream: EventStream,
) : AbstractDiscountWorkflow<Long, Unit>(eventStream) {
    companion object {
        const val PRODUCT_ID = "product-id"
    }

    override fun doExecute(
        discountId: Long,
        context: WorkflowContext,
    ) {
        marketplaceAccessApi.addDiscountProduct(discountId, context.data[PRODUCT_ID] as Long)
    }
}
