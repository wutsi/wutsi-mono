package com.wutsi.blog.client.user

data class AuthenticateResponse(
    val accessToken: String = "",
    val userId: Long = -1,
    val sessionId: Long = -1,
    val loginCount: Long = 0,
)
