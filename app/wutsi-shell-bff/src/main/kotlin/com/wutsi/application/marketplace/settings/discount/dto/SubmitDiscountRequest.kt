package com.wutsi.application.marketplace.settings.discount.dto

data class SubmitDiscountRequest(
    val name: String = "",
    val rate: Int = 0,
    val starts: String = "",
    val ends: String = "",
)
