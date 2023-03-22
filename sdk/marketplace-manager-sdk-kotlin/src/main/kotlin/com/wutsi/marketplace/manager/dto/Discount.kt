package com.wutsi.marketplace.manager.dto

import java.time.OffsetDateTime
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List

public data class Discount(
    public val id: Long = 0,
    public val storeId: Long = 0,
    public val type: String = "",
    public val name: String = "",
    public val rate: Int = 0,
    public val starts: OffsetDateTime? = null,
    public val ends: OffsetDateTime? = null,
    public val allProducts: Boolean = false,
    public val productIds: List<Long> = emptyList(),
    public val created: OffsetDateTime = OffsetDateTime.now(),
    public val updated: OffsetDateTime = OffsetDateTime.now(),
)
