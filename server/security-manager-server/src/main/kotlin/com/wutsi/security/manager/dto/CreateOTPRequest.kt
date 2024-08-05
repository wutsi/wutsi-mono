package com.wutsi.security.manager.dto

import jakarta.validation.constraints.NotBlank
import kotlin.String

public data class CreateOTPRequest(
    @get:NotBlank
    public val address: String = "",
    @get:NotBlank
    public val type: String = "",
)
