package com.wutsi.platform.payment.provider.paypal.model

data class PPAddress(
    val name: String = "",
    val message: String? = null,
    val debug_id: String? = null,
    val details: PPErrorDetails = PPErrorDetails()
)
