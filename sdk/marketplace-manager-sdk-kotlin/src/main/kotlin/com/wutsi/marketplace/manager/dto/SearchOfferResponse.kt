package com.wutsi.marketplace.manager.dto

import kotlin.collections.List

public data class SearchOfferResponse(
    public val offers: List<OfferSummary> = emptyList(),
)
