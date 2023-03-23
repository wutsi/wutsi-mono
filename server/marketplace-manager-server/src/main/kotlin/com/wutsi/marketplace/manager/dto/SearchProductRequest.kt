package com.wutsi.marketplace.manager.dto

import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List

public data class SearchProductRequest(
    public val productIds: List<Long> = emptyList(),
    public val categoryIds: List<Long> = emptyList(),
    public val types: List<String> = emptyList(),
    public val storeId: Long? = null,
    public val limit: Int = 100,
    public val offset: Int = 0,
    public val status: String? = null,
    public val sortBy: String? = null,
)
