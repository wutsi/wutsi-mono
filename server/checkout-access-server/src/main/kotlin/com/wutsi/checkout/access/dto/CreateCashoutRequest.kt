package com.wutsi.checkout.access.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kotlin.Long
import kotlin.String

public data class CreateCashoutRequest(
    @get:NotBlank
    @get:Size(max = 100)
    public val email: String = "",
    @get:NotBlank
    public val paymentMethodToken: String = "",
    public val businessId: Long = 0,
    @get:Min(0)
    public val amount: Long = 0,
    @get:Size(max = 100)
    public val description: String? = null,
    @get:NotBlank
    @get:Size(max = 36)
    public val idempotencyKey: String = "",
)
