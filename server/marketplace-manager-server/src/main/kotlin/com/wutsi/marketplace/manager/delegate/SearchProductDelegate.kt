package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.dto.SearchProductRequest
import com.wutsi.marketplace.manager.dto.SearchProductResponse
import com.wutsi.marketplace.manager.workflow.SearchProductWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class SearchProductDelegate(private val workflow: SearchProductWorkflow) {
    public fun invoke(request: SearchProductRequest): SearchProductResponse =
        workflow.execute(request, WorkflowContext())
}
