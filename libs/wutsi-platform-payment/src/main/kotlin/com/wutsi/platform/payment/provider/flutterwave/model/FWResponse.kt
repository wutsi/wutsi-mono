package com.wutsi.platform.payment.provider.flutterwave.model

data class FWResponse(
    val status: String = "",
    val message: String? = null,
    val data: FWResponseData? = null,
    val code: String? = null,
    val error_id: String? = null,
)
