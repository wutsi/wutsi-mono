package com.wutsi.marketplace.access.dto

import javax.validation.constraints.NotBlank
import kotlin.String

public data class ImportProductRequest(
    @get:NotBlank
    public val url: String = "",
)
