package com.wutsi.membership.manager.dto

import javax.validation.constraints.NotBlank
import kotlin.String

public data class UpdateMemberAttributeRequest(
    @get:NotBlank
    public val name: String = "",
    public val `value`: String? = null,
)
