package com.wutsi.platform.payment.provider.paypal.model

data class PPErrorDetails(
    val issue: String? = null,
    val description: String? = null,
)
