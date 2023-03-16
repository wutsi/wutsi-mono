package com.wutsi.platform.payment.provider.mtn.model

data class MTNRequestToPayRequest(
    val amount: String,
    val currency: String,
    val externalId: String,
    val payeeNote: String,
    val payerMessage: String,
    val payer: MTNParty,
)
