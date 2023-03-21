package com.wutsi.marketplace.access.dto

import kotlin.collections.List

public data class SearchStoreResponse(
    public val stores: List<StoreSummary> = emptyList(),
)
