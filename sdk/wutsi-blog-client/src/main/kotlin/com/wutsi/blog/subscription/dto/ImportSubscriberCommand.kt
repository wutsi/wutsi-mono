package com.wutsi.blog.subscription.dto

data class ImportSubscriberCommand(
    val userId: Long = -1,
    val url: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
