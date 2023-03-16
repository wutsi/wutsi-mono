package com.wutsi.platform.payment.model

data class CreditCard(
    val number: String = "",
    val cvv: String = "",
    val expiryMonth: Int = 0,
    val expiryYear: Int = 0,
    val owner: String = "",
)
