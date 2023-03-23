package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.dto.SearchMeetingProviderResponse
import com.wutsi.marketplace.manager.workflow.SearchMeetingProviderWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class SearchMeetingProviderDelegate(private val workflow: SearchMeetingProviderWorkflow) {
    public fun invoke(): SearchMeetingProviderResponse =
        workflow.execute(null, WorkflowContext())
}
