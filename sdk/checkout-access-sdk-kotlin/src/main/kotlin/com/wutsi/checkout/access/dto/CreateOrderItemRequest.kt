package com.wutsi.checkout.access.dto

import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List

public data class CreateOrderItemRequest(
    public val productId: Long = 0,
    public val productType: String = "",
    public val title: String = "",
    public val quantity: Int = 0,
    public val pictureUrl: String? = null,
    public val unitPrice: Long = 0,
    public val discounts: List<CreateOrderDiscountRequest> = emptyList(),
)
