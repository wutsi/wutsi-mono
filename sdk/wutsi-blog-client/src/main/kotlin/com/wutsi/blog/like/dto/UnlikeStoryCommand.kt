package com.wutsi.blog.like.dto

data class UnlikeStoryCommand(
    val storyId: Long = -1,
    val userId: Long? = null,
    val deviceId: String = "",
)
