package com.wutsi.blog.story.dto

data class StoryAttachmentDownloadedEventPayload(
    val storyId: Long = -1,
    val userId: Long? = null,
    val filename: String = "",
    val subscribe: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
)
