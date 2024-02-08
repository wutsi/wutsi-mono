package com.wutsi.platform.payment.provider.paypal.model

data class PPCreateOrderResponse(
    val id: String = "",
    val status: String = "",
    val links: List<PPLink> = emptyList(),
)