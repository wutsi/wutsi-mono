package com.wutsi.security.manager.dto

import javax.validation.constraints.NotBlank
import kotlin.String

public data class LoginRequest(
    @get:NotBlank
    public val type: String = "",
    @get:NotBlank
    public val username: String = "",
    public val mfaToken: String? = null,
    public val verificationCode: String? = null,
    public val password: String? = null,
)
