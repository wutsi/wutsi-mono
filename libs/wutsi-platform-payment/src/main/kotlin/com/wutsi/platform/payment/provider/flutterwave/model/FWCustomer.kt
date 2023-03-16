package com.wutsi.platform.payment.provider.flutterwave.model

data class FWCustomer(
    val id: Long = -1,
    val name: String? = null,
    val phone_number: String? = null,
    val email: String? = null,
)
