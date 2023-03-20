package com.wutsi.membership.access.dto

import java.time.OffsetDateTime
import kotlin.Long
import kotlin.String

public data class Phone(
    public val id: Long = 0,
    public val number: String = "",
    public val country: String = "",
    public val created: OffsetDateTime = OffsetDateTime.now(),
)
