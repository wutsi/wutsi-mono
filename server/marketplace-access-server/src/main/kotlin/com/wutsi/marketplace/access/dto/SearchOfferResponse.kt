package com.wutsi.marketplace.access.dto

import kotlin.collections.List

public data class SearchOfferResponse(
    public val offers: List<OfferSummary> = emptyList(),
)
