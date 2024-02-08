package com.wutsi.platform.payment.provider.paypal.model

data class PPPayment(
    val captures: List<PPCapture> = emptyList(),
)