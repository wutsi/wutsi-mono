package com.wutsi.platform.payment.model

data class Party(
    val fullName: String = "",
    val phoneNumber: String = "",
    val email: String? = null,
    val country: String? = null,
)
