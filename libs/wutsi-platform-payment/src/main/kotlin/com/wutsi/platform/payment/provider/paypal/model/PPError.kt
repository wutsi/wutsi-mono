package com.wutsi.platform.payment.provider.paypal.model

data class PPError(
    val name: String = "",
    val message: String? = null,
    val debug_id: String? = null,
    val details: List<PPErrorDetails> = emptyList()
)
