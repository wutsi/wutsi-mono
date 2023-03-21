package com.wutsi.marketplace.access.dto

import kotlin.collections.List

public data class SearchDiscountResponse(
    public val discounts: List<DiscountSummary> = emptyList(),
)
