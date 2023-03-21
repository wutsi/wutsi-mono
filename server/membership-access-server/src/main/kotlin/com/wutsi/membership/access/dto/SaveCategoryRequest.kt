package com.wutsi.membership.access.dto

import javax.validation.constraints.NotBlank
import kotlin.String

public data class SaveCategoryRequest(
    @get:NotBlank
    public val title: String = "",
)
