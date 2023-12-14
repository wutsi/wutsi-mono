package com.wutsi.blog.transaction.dto

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class SubmitChargeCommand(
    @NotEmpty val idempotencyKey: String = "",
    @NotNull val productId: Long? = null,
    val userId: Long? = null,
    val email: String? = null,
    val amount: Long = 0,
    val currency: String = "",
    @NotEmpty val paymentMethodOwner: String = "",
    @NotEmpty val paymentNumber: String = "",
    val paymentMethodType: PaymentMethodType = PaymentMethodType.UNKNOWN,
    val timestamp: Long = System.currentTimeMillis(),
)
