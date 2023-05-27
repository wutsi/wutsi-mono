package com.wutsi.blog.comment.dto

data class CommentStoryCommand(
    val storyId: Long = -1,
    val userId: Long = -1,
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
