package com.wutsi.marketplace.access.dto

import javax.validation.constraints.NotBlank
import kotlin.String

public data class UpdateStorePolicyAttributeRequest(
    @get:NotBlank
    public val name: String = "",
    public val `value`: String? = null,
)
