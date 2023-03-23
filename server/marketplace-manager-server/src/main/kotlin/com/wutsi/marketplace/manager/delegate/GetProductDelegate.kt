package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.dto.GetProductResponse
import com.wutsi.marketplace.manager.workflow.GetProductWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class GetProductDelegate(private val workflow: GetProductWorkflow) {
    public fun invoke(id: Long): GetProductResponse =
        workflow.execute(id, WorkflowContext())
}
