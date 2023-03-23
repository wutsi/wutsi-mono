package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.dto.UpdateStorePolicyAttributeRequest
import com.wutsi.marketplace.manager.workflow.UpdateStorePolicyAttributeWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class UpdateStorePolicyAttributeDelegate(private val workflow: UpdateStorePolicyAttributeWorkflow) {
    public fun invoke(id: Long, request: UpdateStorePolicyAttributeRequest) {
        workflow.execute(
            request,
            WorkflowContext(
                data = mutableMapOf(UpdateStorePolicyAttributeWorkflow.ID to id),
            ),
        )
    }
}
