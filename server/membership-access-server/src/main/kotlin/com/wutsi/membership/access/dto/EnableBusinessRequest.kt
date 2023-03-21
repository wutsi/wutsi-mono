package com.wutsi.membership.access.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import kotlin.Boolean
import kotlin.Long
import kotlin.String

public data class EnableBusinessRequest(
    @get:NotBlank
    @get:Size(max = 50)
    public val displayName: String = "",
    @get:NotNull
    public val categoryId: Long = 0,
    @get:NotBlank
    public val country: String = "",
    @get:NotNull
    public val cityId: Long = 0,
    public val street: String? = null,
    public val biography: String? = null,
    @get:NotBlank
    public val email: String = "",
    public val whatsapp: Boolean = false,
)
