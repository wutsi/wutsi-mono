package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.dto.GetStoreResponse
import com.wutsi.marketplace.manager.workflow.GetStoreWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class GetStoreDelegate(private val workflow: GetStoreWorkflow) {
    public fun invoke(id: Long): GetStoreResponse =
        workflow.execute(id, WorkflowContext())
}
