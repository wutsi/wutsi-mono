package com.wutsi.marketplace.manager.dto

import javax.validation.constraints.NotBlank
import kotlin.String

public data class UpdateFundraisingAttributeRequest(
    @get:NotBlank
    public val name: String = "",
    public val `value`: String? = null,
)
