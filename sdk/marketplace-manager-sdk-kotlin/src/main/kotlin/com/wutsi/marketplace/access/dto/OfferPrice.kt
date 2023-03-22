package com.wutsi.marketplace.access.dto

import java.time.OffsetDateTime
import kotlin.Int
import kotlin.Long

public data class OfferPrice(
    public val productId: Long = 0,
    public val price: Long = 0,
    public val referencePrice: Long? = null,
    public val discountId: Long? = null,
    public val savings: Long = 0,
    public val savingsPercentage: Int = 0,
    public val expires: OffsetDateTime? = null,
)
