package com.wutsi.marketplace.manager.dto

import java.time.OffsetDateTime
import kotlin.Long
import kotlin.String

public data class Fundraising(
    public val id: Long = 0,
    public val accountId: Long = 0,
    public val businessId: Long = 0,
    public val currency: String = "",
    public val created: OffsetDateTime = OffsetDateTime.now(),
    public val updated: OffsetDateTime = OffsetDateTime.now(),
    public val deactivated: OffsetDateTime? = null,
    public val status: String = "",
)
