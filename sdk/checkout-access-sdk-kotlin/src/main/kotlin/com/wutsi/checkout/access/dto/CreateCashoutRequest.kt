package com.wutsi.checkout.access.dto

import kotlin.Long
import kotlin.String

public data class CreateCashoutRequest(
    public val email: String = "",
    public val paymentMethodToken: String = "",
    public val businessId: Long = 0,
    public val amount: Long = 0,
    public val description: String? = null,
    public val idempotencyKey: String = "",
)
