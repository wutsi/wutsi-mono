package com.wutsi.security.manager.dto

import javax.validation.constraints.NotBlank
import kotlin.String

public data class VerifyPasswordRequest(
    @get:NotBlank
    public val `value`: String = ""
)
