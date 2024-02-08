package com.wutsi.platform.payment.provider.paypal.model

data class PPPurchaseUnit(
    val reference_id: String = "",
    val description: String? = null,
    val amount: PPMoney = PPMoney(),
    val payments: PPPayment? = null,
    val payee: PPPayee? = null,
)