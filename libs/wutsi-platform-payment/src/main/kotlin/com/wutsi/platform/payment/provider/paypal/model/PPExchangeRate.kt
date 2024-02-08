package com.wutsi.platform.payment.provider.paypal.model

data class PPExchangeRate(
    val source_currency: String = "",
    val target_currency: String = "",
    val value: Double = 0.0,
)
