package com.wutsi.marketplace.access.dto

import org.springframework.format.`annotation`.DateTimeFormat
import java.time.OffsetDateTime
import kotlin.Long
import kotlin.String

public data class Fundraising(
    public val id: Long = 0,
    public val accountId: Long = 0,
    public val businessId: Long = 0,
    public val currency: String = "",
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val created: OffsetDateTime = OffsetDateTime.now(),
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val updated: OffsetDateTime = OffsetDateTime.now(),
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val deactivated: OffsetDateTime? = null,
    public val status: String = "",
    public val amount: Long = 0,
    public val description: String? = null,
    public val videoUrl: String? = null,
)
