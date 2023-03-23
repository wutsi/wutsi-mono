package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.dto.SearchOfferRequest
import com.wutsi.marketplace.manager.dto.SearchOfferResponse
import com.wutsi.marketplace.manager.workflow.SearchOfferWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class SearchOfferDelegate(private val workflow: SearchOfferWorkflow) {
    public fun invoke(request: SearchOfferRequest): SearchOfferResponse =
        workflow.execute(request, WorkflowContext())
}
