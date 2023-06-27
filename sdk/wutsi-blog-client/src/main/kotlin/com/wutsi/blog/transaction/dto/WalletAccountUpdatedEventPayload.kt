package com.wutsi.blog.transaction.dto

data class WalletAccountUpdatedEventPayload(
    val number: String = "",
    val owner: String = "",
    val type: PaymentMethodType = PaymentMethodType.UNKNOWN,
)
