package com.wutsi.platform.payment.provider.paypal.model

data class PPOrderResponse(
    val id: String = "",
    val status: String = "",
    val create_time: String = "",
    val update_time: String = "",
    val payer: PPPayer = PPPayer(),
    val payment_source: PPPaymentSource = PPPaymentSource(),
    val purchase_units: List<PPPurchaseUnit> = emptyList(),
    val links: List<PPLink> = emptyList(),
)