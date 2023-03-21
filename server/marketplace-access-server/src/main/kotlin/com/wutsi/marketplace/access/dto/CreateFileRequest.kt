package com.wutsi.marketplace.access.dto

import javax.validation.constraints.NotBlank
import kotlin.Int
import kotlin.Long
import kotlin.String

public data class CreateFileRequest(
    public val productId: Long = 0,
    @get:NotBlank
    public val name: String = "",
    @get:NotBlank
    public val url: String = "",
    @get:NotBlank
    public val contentType: String = "",
    public val contentSize: Int = 0,
)
