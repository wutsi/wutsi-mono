package com.wutsi.platform.payment.provider.paypal.model

data class PPPaymentSource(
    val paypal: Map<String, Any> = emptyMap(),
)