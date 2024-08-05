package com.wutsi.checkout.access.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kotlin.Long
import kotlin.String

public data class CreateOrderDiscountRequest(
    public val discountId: Long = 0,
    @get:NotBlank
    @get:Size(max = 30)
    public val name: String = "",
    @get:NotBlank
    public val type: String = "",
    public val amount: Long = 0,
)
