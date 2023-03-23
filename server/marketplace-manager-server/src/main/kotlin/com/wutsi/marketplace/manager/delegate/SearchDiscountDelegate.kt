package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.dto.SearchDiscountRequest
import com.wutsi.marketplace.manager.dto.SearchDiscountResponse
import com.wutsi.marketplace.manager.workflow.SearchDiscountWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class SearchDiscountDelegate(private val workflow: SearchDiscountWorkflow) {
    public fun invoke(request: SearchDiscountRequest): SearchDiscountResponse =
        workflow.execute(request, WorkflowContext())
}
