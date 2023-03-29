package com.wutsi.event

@Deprecated("")
data class PaymentMethodEventPayload(
    val accountId: Long = -1,
    val paymentMethodToken: String = "",
)
