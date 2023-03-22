package com.wutsi.membership.manager.delegate

import com.wutsi.membership.manager.dto.SearchCategoryRequest
import com.wutsi.membership.manager.dto.SearchCategoryResponse
import com.wutsi.membership.manager.workflow.SearchCategoryWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class SearchCategoryDelegate(private val workflow: SearchCategoryWorkflow) {
    public fun invoke(request: SearchCategoryRequest): SearchCategoryResponse {
        val context = WorkflowContext(input = request)
        workflow.execute(context)
        return context.output as SearchCategoryResponse
    }
}
