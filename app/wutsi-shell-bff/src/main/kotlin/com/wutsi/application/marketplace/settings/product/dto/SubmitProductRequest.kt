package com.wutsi.application.marketplace.settings.product.dto

data class SubmitProductRequest(
    val title: String = "",
    val summary: String = "",
    val price: Long = 0,
    val quantity: String = "",
)
