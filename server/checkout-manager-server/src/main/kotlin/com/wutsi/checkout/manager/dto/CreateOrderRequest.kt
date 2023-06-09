package com.wutsi.checkout.manager.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
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
    @get:NotBlank
    public val customerName: String = "",
    @get:NotBlank
    @get:Size(max = 100)
    public val customerEmail: String = "",
    @get:NotNull
    @get:NotEmpty
    public val items: List<CreateOrderItemRequest> = emptyList(),
    public val discounts: List<CreateOrderDiscountRequest> = emptyList(),
)
