package com.wutsi.event

data class PaymentMethodEventPayload(
    val accountId: Long = -1,
    val paymentMethodToken: String = "",
)
