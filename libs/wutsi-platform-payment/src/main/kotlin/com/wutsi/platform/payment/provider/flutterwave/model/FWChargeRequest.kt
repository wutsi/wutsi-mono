package com.wutsi.platform.payment.provider.flutterwave.model

data class FWChargeRequest(
    val amount: String,
    val currency: String,
    val email: String,
    val tx_ref: String,
    val phone_number: String? = null,
    val country: String? = null,
    val fullname: String? = null,
    val device_fingerprint: String? = null,
    val card_number: String? = null,
    val cvv: String? = null,
    val expiry_month: String? = null,
    var expiry_year: String? = null,
    var preauthorize: Boolean? = null,
)
