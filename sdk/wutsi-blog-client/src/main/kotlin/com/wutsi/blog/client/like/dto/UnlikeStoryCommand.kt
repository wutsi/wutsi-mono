package com.wutsi.blog.client.like.dto

data class UnlikeStoryCommand(
    val storyId: Long = -1,
    val userId: Long? = null,
    val deviceId: String = "",
)
