package com.wutsi.membership.access.dto

import javax.validation.constraints.NotBlank
import kotlin.String

public data class UpdateAccountAttributeRequest(
    @get:NotBlank
    public val name: String = "",
    public val `value`: String? = null,
)
