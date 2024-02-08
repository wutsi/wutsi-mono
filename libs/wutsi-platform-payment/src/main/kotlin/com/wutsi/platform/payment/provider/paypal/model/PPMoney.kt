package com.wutsi.platform.payment.provider.paypal.model

data class PPMoney(
    val value: Double = 0.0,
    val currency_code: String = "",
)