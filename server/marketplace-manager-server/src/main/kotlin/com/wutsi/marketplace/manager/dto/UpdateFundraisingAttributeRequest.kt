package com.wutsi.marketplace.manager.dto

import jakarta.validation.constraints.NotBlank
import kotlin.String

public data class UpdateFundraisingAttributeRequest(
    @get:NotBlank
    public val name: String = "",
    public val `value`: String? = null,
)
