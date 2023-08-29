package com.wutsi.platform.payment.model

import com.wutsi.platform.payment.core.Money

data class CreatePaymentRequest(
    val walletId: String? = null,
    val payer: Party,
    val amount: Money,
    val externalId: String,
    val description: String,
    val payerMessage: String?,
    val deviceId: String? = null,
    val bankAccount: BankAccount? = null,
    val creditCard: CreditCard? = null,
)
