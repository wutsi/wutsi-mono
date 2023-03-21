package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.SearchMeetingProviderResponse
import com.wutsi.marketplace.access.service.MeetingProviderService
import org.springframework.stereotype.Service

@Service
public class SearchMeetingProviderDelegate(private val service: MeetingProviderService) {
    public fun invoke(): SearchMeetingProviderResponse {
        val providers = service.search()
        return SearchMeetingProviderResponse(
            meetingProviders = providers.map {
                service.toMeetingProviderSummary(it)
            },
        )
    }
}
