package com.wutsi.platform.payment.provider.paypal.model

data class PPPayer(
    val payer_id: String = "",
    val name: PPName = PPName(),
    val email_address: String = "",
    val address: PPAddress = PPAddress(),
)