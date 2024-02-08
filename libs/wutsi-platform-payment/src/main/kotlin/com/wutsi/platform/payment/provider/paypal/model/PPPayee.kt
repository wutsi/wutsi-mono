package com.wutsi.platform.payment.provider.paypal.model

data class PPPayee(
    val merchant_id: String = "",
    val email_address: String = "",
)