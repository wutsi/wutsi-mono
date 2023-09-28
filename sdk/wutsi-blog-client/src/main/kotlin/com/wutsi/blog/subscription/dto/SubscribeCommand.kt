package com.wutsi.blog.subscription.dto

data class SubscribeCommand(
    val userId: Long = -1,
    val subscriberId: Long = -1,
    val email: String? = null,
    val storyId: Long? = null,
    val referer: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
