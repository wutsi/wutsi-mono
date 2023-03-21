package com.wutsi.checkout.access.dto

import java.time.OffsetDateTime
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List

public data class SearchOrderRequest(
    public val businessId: Long? = null,
    public val customerAccountId: Long? = null,
    public val productId: Long? = null,
    public val status: List<String> = emptyList(),
    public val createdFrom: OffsetDateTime? = null,
    public val createdTo: OffsetDateTime? = null,
    public val expiresTo: OffsetDateTime? = null,
    public val limit: Int = 100,
    public val offset: Int = 0,
)
