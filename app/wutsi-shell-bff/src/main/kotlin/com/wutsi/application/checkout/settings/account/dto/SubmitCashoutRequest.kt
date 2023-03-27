package com.wutsi.application.checkout.settings.account.dto

data class SubmitCashoutRequest(
    val amount: Long = 0,
    val token: String = "",
)
