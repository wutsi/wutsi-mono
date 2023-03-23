package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.dto.GetCategoryResponse
import com.wutsi.marketplace.manager.workflow.GetCategoryWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class GetCategoryDelegate(private val workflow: GetCategoryWorkflow) {
    public fun invoke(id: Long): GetCategoryResponse =
        workflow.execute(id, WorkflowContext())
}
