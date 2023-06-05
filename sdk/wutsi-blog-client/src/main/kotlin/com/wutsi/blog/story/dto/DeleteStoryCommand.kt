package com.wutsi.blog.story.dto

data class DeleteStoryCommand(
    val storyId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
