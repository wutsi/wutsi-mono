package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.dto.GetOfferResponse
import com.wutsi.marketplace.manager.workflow.GetOfferWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class GetOfferDelegate(private val workflow: GetOfferWorkflow) {
    public fun invoke(id: Long): GetOfferResponse =
        workflow.execute(id, WorkflowContext())
}
