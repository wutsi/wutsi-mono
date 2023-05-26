package com.wutsi.blog.client.like.dto

data class StoryLikedEvent(
    val storyId: Long = -1,
    val userId: Long? = null,
    val deviceId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
