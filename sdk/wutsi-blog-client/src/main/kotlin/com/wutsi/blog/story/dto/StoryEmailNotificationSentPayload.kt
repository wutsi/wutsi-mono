package com.wutsi.blog.story.dto

data class StoryEmailNotificationSentPayload(
    val messageId: String = "",
    val email: String? = null,
)
