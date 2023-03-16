package com.wutsi.platform.payment.provider.flutterwave.model

data class FWTransferRequest(
    val account_bank: String,
    val account_number: String,
    val amount: String,
    val currency: String,
    val beneficiary_name: String = "",
    val narration: String = "",
    val reference: String = "",
    val email: String = "",
    val meta: Map<String, String?>? = null,
)
