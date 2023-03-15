package com.wutsi.platform.core.messaging.whatsapp

data class WAError(
    val code: Int = -1,
    val error_subcode: Int = -1,
    val message: String = "",
    val type: String = "",
)
