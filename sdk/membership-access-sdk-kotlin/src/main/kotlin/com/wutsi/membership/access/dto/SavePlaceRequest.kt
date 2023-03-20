package com.wutsi.membership.access.dto

import kotlin.Double
import kotlin.Long
import kotlin.String

public data class SavePlaceRequest(
    public val id: Long = 0,
    public val name: String = "",
    public val country: String = "",
    public val type: String = "",
    public val longitude: Double? = null,
    public val latitude: Double? = null,
    public val timezoneId: String? = null,
)
