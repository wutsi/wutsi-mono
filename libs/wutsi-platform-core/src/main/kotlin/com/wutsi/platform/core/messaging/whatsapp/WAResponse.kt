package com.wutsi.platform.core.messaging.whatsapp

data class WAResponse(
    val messaging_product: String = "whatsapp",
    val contacts: List<WAContact> = emptyList(),
    val messages: List<WAMessageID> = emptyList(),
)
