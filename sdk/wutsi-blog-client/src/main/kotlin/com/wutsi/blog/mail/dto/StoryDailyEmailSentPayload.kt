package com.wutsi.blog.mail.dto

data class StoryDailyEmailSentPayload(
    val messageId: String = "",
    val email: String? = null,
)
