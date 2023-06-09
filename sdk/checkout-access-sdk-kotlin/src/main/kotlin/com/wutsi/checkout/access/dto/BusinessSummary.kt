package com.wutsi.checkout.access.dto

import java.time.OffsetDateTime
import kotlin.Long
import kotlin.String

public data class BusinessSummary(
    public val id: Long = 0,
    public val accountId: Long = 0,
    public val balance: Long = 0,
    public val country: String = "",
    public val currency: String = "",
    public val status: String = "",
    public val created: OffsetDateTime = OffsetDateTime.now(),
)
