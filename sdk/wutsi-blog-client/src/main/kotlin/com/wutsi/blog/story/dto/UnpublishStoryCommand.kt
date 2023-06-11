package com.wutsi.blog.story.dto

data class UnpublishStoryCommand(
    val storyId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
