package com.wutsi.marketplace.access.dto

import org.springframework.format.`annotation`.DateTimeFormat
import java.time.LocalDate
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List

public data class SearchDiscountRequest(
    public val storeId: Long? = null,
    public val productIds: List<Long> = emptyList(),
    public val discountIds: List<Long> = emptyList(),
    public val type: String? = null,
    @get:DateTimeFormat(pattern = "yyyy-MM-dd")
    public val date: LocalDate? = null,
    public val limit: Int = 100,
    public val offset: Int = 0,
)
