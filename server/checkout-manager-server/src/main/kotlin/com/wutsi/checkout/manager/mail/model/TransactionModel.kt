package com.wutsi.checkout.manager.mail.model

data class TransactionModel(
    val id: String,
    val type: String,
    val amount: String,
    val paymentMethod: PaymentMethodModel,
)
