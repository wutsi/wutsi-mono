package com.wutsi.blog.story.dto

data class SendStoryEmailNotificationCommand(
    val storyId: Long,
    val recipientId: Long,
)
