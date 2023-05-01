package com.wutsi.blog.client.telegram

data class CheckBotAccessResponse(
    val chatId: String = "",
    val chatName: String = "",
    val pictureUrl: String? = null,
)
