package com.wutsi.platform.payment.provider.paypal.model

data class PPPurchaseUnitItem(
    val name: String = "",
    val quantity: Int = 1,
    val description: String? = null,
    val unit_amount: PPMoney = PPMoney(),
)