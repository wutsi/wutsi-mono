package com.wutsi.checkout.access.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kotlin.Long
import kotlin.String

public data class CreateChargeRequest(
    @get:NotBlank
    @get:Size(max = 100)
    public val email: String = "",
    public val paymentMethodType: String? = null,
    @get:Size(max = 100)
    public val paymentMethodOwnerName: String? = null,
    public val paymentProviderId: Long? = null,
    public val paymentMethodToken: String? = null,
    @get:Size(max = 30)
    public val paymenMethodNumber: String? = null,
    public val businessId: Long = 0,
    @get:Min(0)
    public val amount: Long = 0,
    @get:NotBlank
    public val orderId: String = "",
    @get:Size(max = 100)
    public val description: String? = null,
    @get:NotBlank
    @get:Size(max = 36)
    public val idempotencyKey: String = "",
)
