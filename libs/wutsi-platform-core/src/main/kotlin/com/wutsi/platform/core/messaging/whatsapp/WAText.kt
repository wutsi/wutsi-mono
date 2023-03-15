package com.wutsi.platform.core.messaging.whatsapp

data class WAText(
    val body: String,
    val preview_url: Boolean = true,
)
