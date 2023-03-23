package com.wutsi.marketplace.manager.workflow

import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class DeleteDiscountWorkflow(
    eventStream: EventStream,
) : AbstractDiscountWorkflow<Long, Unit>(eventStream) {
    override fun doExecute(discountId: Long, context: WorkflowContext) {
        marketplaceAccessApi.deleteDiscount(discountId)
    }
}
