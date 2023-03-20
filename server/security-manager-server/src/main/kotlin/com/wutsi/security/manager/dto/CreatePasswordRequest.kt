package com.wutsi.security.manager.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import kotlin.Long
import kotlin.String

public data class CreatePasswordRequest(
    public val accountId: Long = 0,
    @get:NotBlank
    @get:Size(max = 30)
    public val username: String = "",
    @get:NotBlank
    public val `value`: String = ""
)
