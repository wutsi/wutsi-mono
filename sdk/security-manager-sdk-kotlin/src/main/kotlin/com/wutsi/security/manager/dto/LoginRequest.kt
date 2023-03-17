package com.wutsi.security.manager.dto

import kotlin.String

public data class LoginRequest(
    public val type: String = "",
    public val username: String = "",
    public val mfaToken: String? = null,
    public val verificationCode: String? = null,
    public val password: String? = null,
)
