package com.wutsi.blog.transaction.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class SubmitPaymentCommand(
    @NotEmpty val idempotencyKey: String = "",
    @NotNull val adsId: String? = null,
    val userId: Long? = null,
    val email: String? = null,
    val amount: Long = 0,
    val currency: String = "",
    @NotEmpty val paymentMethodOwner: String = "",
    val paymentNumber: String = "",
    val paymentMethodType: PaymentMethodType = PaymentMethodType.UNKNOWN,
    val timestamp: Long = System.currentTimeMillis(),
    val internationalCurrency: String? = null,
    val channel: String? = null,
)
