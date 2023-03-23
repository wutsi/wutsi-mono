package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.dto.CreateDiscountRequest
import com.wutsi.marketplace.manager.dto.CreateDiscountResponse
import com.wutsi.marketplace.manager.workflow.CreateDiscountWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class CreateDiscountDelegate(private val workflow: CreateDiscountWorkflow) {
    public fun invoke(request: CreateDiscountRequest): CreateDiscountResponse =
        workflow.execute(request, WorkflowContext())
}
