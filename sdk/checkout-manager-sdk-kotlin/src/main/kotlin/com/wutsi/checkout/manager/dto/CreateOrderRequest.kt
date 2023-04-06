package com.wutsi.checkout.manager.dto

import kotlin.Long
import kotlin.String
import kotlin.collections.List

public data class CreateOrderRequest(
    public val type: String = "",
    public val deviceType: String? = null,
    public val channelType: String? = null,
    public val businessId: Long = 0,
    public val notes: String? = null,
    public val customerAccountId: Long? = null,
    public val customerName: String = "",
    public val customerEmail: String = "",
    public val items: List<CreateOrderItemRequest> = emptyList(),
    public val discounts: List<CreateOrderDiscountRequest> = emptyList(),
)
