package com.wutsi.platform.payment.provider.paypal.model

data class PPPaymentSourcePaypal(
    val name: PPName = PPName(),
    val email_address: String = "",
    val account_id: String = "",
    val account_status: String = "",
    val purchase_units: List<PPPurchaseUnit> = emptyList(),
)