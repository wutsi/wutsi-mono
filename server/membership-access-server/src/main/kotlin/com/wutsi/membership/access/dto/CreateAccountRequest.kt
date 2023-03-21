package com.wutsi.membership.access.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import kotlin.Long
import kotlin.String

public data class CreateAccountRequest(
    @get:NotBlank
    public val phoneNumber: String = "",
    @get:NotBlank
    @get:Size(max = 2)
    public val language: String = "",
    @get:NotBlank
    @get:Size(
        min = 2,
        max = 2,
    )
    public val country: String = "",
    @get:NotBlank
    @get:Size(max = 50)
    public val displayName: String = "",
    public val pictureUrl: String? = null,
    public val cityId: Long? = null,
)
