package com.wutsi.blog.like.dto

data class LikeStoryCommand(
    val storyId: Long = -1,
    val userId: Long? = null,
    val deviceId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
