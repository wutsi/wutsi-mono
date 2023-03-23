package com.wutsi.marketplace.manager.dto

import javax.validation.constraints.NotBlank
import kotlin.Long
import kotlin.String

public data class UpdateProductAttributeRequest(
    public val productId: Long = 0,
    @get:NotBlank
    public val name: String = "",
    public val `value`: String? = null,
)
