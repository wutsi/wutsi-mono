package com.wutsi.blog.story.domain

data class ViewEntity(
    val userId: Long? = null,
    val deviceId: String = "",
    val storyId: Long = -1,
)
