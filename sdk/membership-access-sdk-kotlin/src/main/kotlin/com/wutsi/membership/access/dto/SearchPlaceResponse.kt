package com.wutsi.membership.access.dto

import kotlin.collections.List

public data class SearchPlaceResponse(
    public val places: List<PlaceSummary> = emptyList(),
)
