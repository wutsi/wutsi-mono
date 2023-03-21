package com.wutsi.marketplace.access.dto

import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List

public data class SearchOfferRequest(
    public val storeId: Long? = null,
    public val productIds: List<Long> = emptyList(),
    public val types: List<String> = emptyList(),
    public val limit: Int = 100,
    public val offset: Int = 0,
    public val sortBy: String? = null,
)
