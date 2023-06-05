package com.wutsi.blog.story.dto

data class UpdateStoryCommand(
    val storyId: Long = -1,
    val title: String? = null,
    val content: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
