package com.wutsi.platform.payment.provider.paypal.model

data class PPAuthResponse(
    val scope: String = "",
    val access_token: String = "",
    val token_type: String = "",
    val app_id: String = "",
    val expires_in: Int = -1,
    val nounce: String = "",
)