package com.wutsi.platform.payment.provider.paypal.model

data class PPCreateOrderRequest(
    val intent: String = "",
    val purchase_units: List<PPPurchaseUnit> = emptyList(),
)