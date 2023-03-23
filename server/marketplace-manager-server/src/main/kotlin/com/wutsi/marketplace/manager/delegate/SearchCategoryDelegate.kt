package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.dto.SearchCategoryRequest
import com.wutsi.marketplace.manager.dto.SearchCategoryResponse
import com.wutsi.marketplace.manager.workflow.SearchCategoryWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class SearchCategoryDelegate(private val workflow: SearchCategoryWorkflow) {
    public fun invoke(request: SearchCategoryRequest): SearchCategoryResponse =
        workflow.execute(request, WorkflowContext())
}
