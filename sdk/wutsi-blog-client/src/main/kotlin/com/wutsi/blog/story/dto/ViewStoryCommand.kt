package com.wutsi.blog.story.dto

data class ViewStoryCommand(
    val storyId: Long = -1,
    val userId: Long? = null,
    val deviceId: String = "",
    val readTimeMillis: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
