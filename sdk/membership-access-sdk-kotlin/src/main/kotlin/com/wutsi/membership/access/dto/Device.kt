package com.wutsi.membership.access.dto

import java.time.OffsetDateTime
import kotlin.String

public data class Device(
    public val token: String = "",
    public val type: String? = null,
    public val model: String? = null,
    public val osName: String? = null,
    public val osVersion: String? = null,
    public val created: OffsetDateTime = OffsetDateTime.now(),
    public val updated: OffsetDateTime = OffsetDateTime.now(),
)
