package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.workflow.RemoveDiscountProductWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class RemoveDiscountProductDelegate(private val workflow: RemoveDiscountProductWorkflow) {
    public fun invoke(discountId: Long, productId: Long) {
        workflow.execute(
            discountId,
            WorkflowContext(data = mutableMapOf(RemoveDiscountProductWorkflow.PRODUCT_ID to productId)),
        )
    }
}
