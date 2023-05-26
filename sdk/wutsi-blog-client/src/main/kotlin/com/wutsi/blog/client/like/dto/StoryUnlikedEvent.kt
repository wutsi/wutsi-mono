package com.wutsi.blog.client.like.dto

data class StoryUnlikedEvent(
    val storyId: Long = -1,
    val userId: Long? = null,
    val deviceId: String = "",
)
