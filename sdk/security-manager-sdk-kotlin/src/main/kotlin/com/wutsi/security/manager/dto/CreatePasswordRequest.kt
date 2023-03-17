package com.wutsi.security.manager.dto

import kotlin.Long
import kotlin.String

public data class CreatePasswordRequest(
    public val accountId: Long = 0,
    public val username: String = "",
    public val `value`: String = "",
)
