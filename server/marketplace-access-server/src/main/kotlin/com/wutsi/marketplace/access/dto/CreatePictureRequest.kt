package com.wutsi.marketplace.access.dto

import javax.validation.constraints.NotBlank
import kotlin.Long
import kotlin.String

public data class CreatePictureRequest(
    public val productId: Long = 0,
    @get:NotBlank
    public val url: String = "",
)
