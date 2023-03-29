package com.wutsi.checkout.manager.mail.model

data class PaymentProviderModel(
    val id: Long,
    val code: String,
    val name: String,
    val logoUrl: String,
)
