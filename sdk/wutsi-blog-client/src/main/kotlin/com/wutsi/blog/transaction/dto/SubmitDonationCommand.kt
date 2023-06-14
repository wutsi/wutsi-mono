package com.wutsi.blog.transaction.dto

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class DonateCommand(
    @NotNull val merchantId: Long? = null,
    val userId: Long? = null,
    val email: String? = null,
    val amount: Double = 0.0,
    val currency: String = "",
    @NotEmpty val paymentMethodOwner: String = "",
    val paymentMethodType: PaymentMethodType = PaymentMethodType.UNKNOWN,
    @NotEmpty val paymentNumber: String = "",
    val anonymous: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
)
