package com.wutsi.checkout.access.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kotlin.Long
import kotlin.String

public data class CreateBusinessRequest(
    public val accountId: Long = 0,
    @get:NotBlank
    @get:Size(
        min = 2,
        max = 2,
    )
    public val country: String = "",
    @get:NotBlank
    @get:Size(
        min = 3,
        max = 3,
    )
    public val currency: String = "",
)
