package com.wutsi.blog.transaction.dto

import com.wutsi.blog.client.channel.ChannelType
import javax.validation.constraints.NotEmpty

data class SubmitDonationCommand(
    @NotEmpty val idempotencyKey: String = "",
    @NotEmpty val walletId: String = "",
    val userId: Long? = null,
    val email: String? = null,
    val amount: Long = 0,
    val currency: String = "",
    @NotEmpty val paymentMethodOwner: String = "",
    @NotEmpty val paymentNumber: String = "",
    val paymentMethodType: PaymentMethodType = PaymentMethodType.UNKNOWN,
    val anonymous: Boolean = false,
    val description: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val internationalCurrency: String? = null,
    val channel: ChannelType? = null,
)
