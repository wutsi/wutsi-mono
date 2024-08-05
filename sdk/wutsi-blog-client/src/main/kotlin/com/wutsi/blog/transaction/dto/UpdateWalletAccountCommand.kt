package com.wutsi.blog.transaction.dto

import jakarta.validation.constraints.NotEmpty

data class UpdateWalletAccountCommand(
    @NotEmpty val walletId: String = "",
    @NotEmpty val number: String = "",
    val owner: String? = null,
    val type: PaymentMethodType = PaymentMethodType.UNKNOWN,
    val timestamp: Long = System.currentTimeMillis(),
)
