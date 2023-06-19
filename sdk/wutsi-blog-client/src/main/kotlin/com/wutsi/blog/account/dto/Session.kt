package com.wutsi.blog.account.dto

import java.util.Date

data class Session(
    val accessToken: String = "",
    val refreshToken: String? = null,
    val accountId: Long = -1,
    val userId: Long = -1,
    val runAsUserId: Long? = null,
    val loginDateTime: Date = Date(),
    var logoutDateTime: Date? = null,
)
