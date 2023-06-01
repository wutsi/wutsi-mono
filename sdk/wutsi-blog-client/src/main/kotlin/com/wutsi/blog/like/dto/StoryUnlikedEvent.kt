package com.wutsi.blog.like.dto

data class StoryUnlikedEvent(
    val storyId: Long = -1,
    val userId: Long? = null,
    val deviceId: String? = null,
)
