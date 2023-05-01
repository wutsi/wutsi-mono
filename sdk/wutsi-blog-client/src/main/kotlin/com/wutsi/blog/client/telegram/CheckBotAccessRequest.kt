package com.wutsi.blog.client.telegram

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class CheckBotAccessRequest(
    @get:NotEmpty val username: String = "",
    @get:NotEmpty val chatTitle: String = "",
    @get:NotNull val chatType: TelegramChatType? = null,
)
