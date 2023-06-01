package com.wutsi.blog.like.dto

data class StoryLikedEvent(
    val storyId: Long = -1,
    val userId: Long? = null,
    val deviceId: String? = null,
)
