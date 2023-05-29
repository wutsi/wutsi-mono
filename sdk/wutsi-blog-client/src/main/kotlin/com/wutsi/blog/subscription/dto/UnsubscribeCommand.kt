package com.wutsi.blog.subscription.dto

data class UnsubscribeCommand(
    val userId: Long = -1,
    val subscriberId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
