package com.wutsi.platform.payment.provider.mtn.model

data class MTNTransferResponse(
    val amount: String = "",
    val currency: String = "",
    val financialTransactionId: String = "",
    val payee: MTNParty = MTNParty(),
    val payeeNote: String = "",
    val payerMessage: String = "",
    val externalId: String = "",
    val status: String = "",
    val reason: String? = null,
)
