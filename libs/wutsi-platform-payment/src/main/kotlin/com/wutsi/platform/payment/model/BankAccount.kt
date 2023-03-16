package com.wutsi.platform.payment.model

data class BankAccount(
    val number: String = "",
    val bankCode: String = "",
    val bankName: String = "",
    val country: String = "",
    val owner: String = "",
)
