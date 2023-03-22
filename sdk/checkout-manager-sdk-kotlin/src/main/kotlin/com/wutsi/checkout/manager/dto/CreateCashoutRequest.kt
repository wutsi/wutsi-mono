package com.wutsi.checkout.manager.dto

import kotlin.Long
import kotlin.String

public data class CreateCashoutRequest(
    public val paymentMethodToken: String = "",
    public val amount: Long = 0,
    public val description: String? = null,
    public val idempotencyKey: String = "",
)
