package com.wutsi.platform.payment.provider.flutterwave.model

data class FWWebhookRequest(
    val event: String = "",
    val data: FWResponseData = FWResponseData(),
)
