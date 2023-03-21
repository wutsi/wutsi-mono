package com.wutsi.membership.access.dto

import kotlin.Long
import kotlin.String

public data class PlaceSummary(
    public val id: Long = 0,
    public val name: String = "",
    public val longName: String = "",
    public val country: String = "",
    public val type: String = "",
)
