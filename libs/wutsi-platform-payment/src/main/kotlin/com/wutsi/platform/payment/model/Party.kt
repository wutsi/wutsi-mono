package com.wutsi.platform.payment.model

data class Party(
    val id: String? = null,
    val fullName: String = "",
    val phoneNumber: String = "",
    val email: String? = null,
    val country: String? = null,
)
