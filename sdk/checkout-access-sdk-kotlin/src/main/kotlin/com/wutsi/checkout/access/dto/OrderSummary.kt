package com.wutsi.checkout.access.dto

import java.time.OffsetDateTime
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List

public data class OrderSummary(
    public val id: String = "",
    public val shortId: String = "",
    public val businessId: Long = 0,
    public val type: String = "",
    public val status: String = "",
    public val totalPrice: Long = 0,
    public val balance: Long = 0,
    public val currency: String = "",
    public val created: OffsetDateTime = OffsetDateTime.now(),
    public val customerAccountId: Long? = null,
    public val customerName: String = "",
    public val customerEmail: String = "",
    public val itemCount: Int = 0,
    public val productPictureUrls: List<String> = emptyList(),
)
