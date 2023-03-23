package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.dto.UpdateDiscountAttributeRequest
import com.wutsi.marketplace.manager.workflow.UpdateDiscountAttributeWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class UpdateDiscountAttributeDelegate(private val workflow: UpdateDiscountAttributeWorkflow) {
    public fun invoke(id: Long, request: UpdateDiscountAttributeRequest) {
        workflow.execute(
            request,
            WorkflowContext(data = mutableMapOf(UpdateDiscountAttributeWorkflow.ID to id)),
        )
    }
}
