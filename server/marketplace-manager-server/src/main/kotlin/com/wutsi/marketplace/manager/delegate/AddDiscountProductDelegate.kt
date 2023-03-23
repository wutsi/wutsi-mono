package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.workflow.AddDiscountProductWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class AddDiscountProductDelegate(private val workflow: AddDiscountProductWorkflow) {
    public fun invoke(discountId: Long, productId: Long) {
        workflow.execute(
            discountId,
            WorkflowContext(data = mutableMapOf(AddDiscountProductWorkflow.PRODUCT_ID to productId)),
        )
    }
}
