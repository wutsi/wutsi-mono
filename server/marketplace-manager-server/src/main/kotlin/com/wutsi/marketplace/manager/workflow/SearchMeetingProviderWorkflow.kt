package com.wutsi.marketplace.manager.workflow

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.MeetingProviderSummary
import com.wutsi.marketplace.manager.dto.SearchMeetingProviderResponse
import com.wutsi.workflow.Workflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class SearchMeetingProviderWorkflow(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) : Workflow<Void?, SearchMeetingProviderResponse> {
    override fun execute(request: Void?, context: WorkflowContext): SearchMeetingProviderResponse {
        val response = marketplaceAccessApi.searchMeetingProvider()
        return SearchMeetingProviderResponse(
            meetingProviders = response.meetingProviders.map {
                objectMapper.readValue(
                    objectMapper.writeValueAsString(it),
                    MeetingProviderSummary::class.java,
                )
            },
        )
    }
}
