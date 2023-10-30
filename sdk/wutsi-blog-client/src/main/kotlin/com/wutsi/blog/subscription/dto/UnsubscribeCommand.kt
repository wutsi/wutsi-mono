package com.wutsi.blog.subscription.dto

data class UnsubscribeCommand(
    val userId: Long = -1,
    val subscriberId: Long = -1,
    val email: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
