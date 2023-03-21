package com.wutsi.marketplace.access.dto

import kotlin.collections.List

public data class SearchMeetingProviderResponse(
    public val meetingProviders: List<MeetingProviderSummary> = emptyList(),
)
