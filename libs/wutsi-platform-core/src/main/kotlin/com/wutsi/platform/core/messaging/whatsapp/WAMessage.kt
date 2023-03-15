package com.wutsi.platform.core.messaging.whatsapp

data class WAMessage(
    val messaging_product: String = "whatsapp",
    val recipient_type: String = "individual",
    val type: String = "text",
    val to: String,
    val text: WAText,
)
