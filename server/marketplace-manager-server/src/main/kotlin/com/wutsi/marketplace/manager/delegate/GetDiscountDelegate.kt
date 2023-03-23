package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.dto.GetDiscountResponse
import com.wutsi.marketplace.manager.workflow.GetDiscountWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class GetDiscountDelegate(private val workflow: GetDiscountWorkflow) {
    public fun invoke(id: Long): GetDiscountResponse =
        workflow.execute(id, WorkflowContext())
}
