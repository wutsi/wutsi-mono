package com.wutsi.membership.access.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import kotlin.Double
import kotlin.Long
import kotlin.String

public data class SavePlaceRequest(
    public val id: Long = 0,
    @get:NotBlank
    public val name: String = "",
    @get:NotBlank
    @get:Size(max = 2)
    public val country: String = "",
    @get:NotBlank
    public val type: String = "",
    public val longitude: Double? = null,
    public val latitude: Double? = null,
    public val timezoneId: String? = null,
)
