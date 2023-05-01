package com.wutsi.blog.client.user

import java.util.Date

data class AccountDto(
    val id: Long = -1,
    val provider: String = "",
    val providerUserId: String = "",
    var loginCount: Long = 0,
    var lastLoginDateTime: Date? = null,
    val creationDateTime: Date = Date(),
    var modificationDateTime: Date = Date(),
)
