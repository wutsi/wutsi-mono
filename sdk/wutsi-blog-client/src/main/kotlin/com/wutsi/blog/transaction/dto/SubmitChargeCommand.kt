package com.wutsi.blog.transaction.dto

import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.product.dto.DiscountType
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
    val paymentNumber: String = "",
    val paymentMethodType: PaymentMethodType = PaymentMethodType.UNKNOWN,
    val timestamp: Long = System.currentTimeMillis(),
    val discountType: DiscountType? = null,
    val couponId: Long? = null,
    val internationalCurrency: String? = null,
    val channel: ChannelType? = null,
)
