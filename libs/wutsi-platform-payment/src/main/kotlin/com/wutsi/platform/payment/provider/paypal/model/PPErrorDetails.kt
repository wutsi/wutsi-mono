package com.wutsi.platform.payment.provider.paypal.model

data class PPErrorDetails(
    val field: String? = null,
    val value: String? = null,
    val issue: String? = null,
    val description: String? = null,
)
