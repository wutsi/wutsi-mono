package com.wutsi.blog.transaction.dto

import jakarta.validation.constraints.NotEmpty

data class SubmitCashoutCommand(
    @NotEmpty val walletId: String = "",
    @NotEmpty val idempotencyKey: String = "",
    val amount: Long = 0,
    val currency: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
