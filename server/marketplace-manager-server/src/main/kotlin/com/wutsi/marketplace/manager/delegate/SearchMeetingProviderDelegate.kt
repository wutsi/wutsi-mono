package com.wutsi.marketplace.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.MeetingProviderSummary
import com.wutsi.marketplace.manager.dto.SearchMeetingProviderResponse
import org.springframework.stereotype.Service

@Service
public class SearchMeetingProviderDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) {
    public fun invoke(): SearchMeetingProviderResponse {
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
