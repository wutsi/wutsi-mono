package com.wutsi.platform.core.messaging

data class Party(
    val email: String = "",
    val phoneNumber: String = "",
    val displayName: String? = null,
    val deviceToken: String? = null,
)
