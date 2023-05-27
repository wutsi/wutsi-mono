package com.wutsi.blog.comment.dto

data class StoryCommentedEvent(
    val storyId: Long = -1,
    val userId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
