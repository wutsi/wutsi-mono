package com.wutsi.marketplace.access.dto

import kotlin.Long
import kotlin.String

public data class MeetingProviderSummary(
    public val id: Long = 0,
    public val type: String = "",
    public val name: String = "",
    public val logoUrl: String = "",
)
