package com.wutsi.blog.client.event

data class LoginEvent(
    val sessionId: Long,
    val userId: Long,
    val loginCount: Long,
    val deviceUID: String?,
    val userAgent: String?,
)
