package com.wutsi.marketplace.access.dto

import org.springframework.format.`annotation`.DateTimeFormat
import java.time.OffsetDateTime
import kotlin.Int
import kotlin.Long
import kotlin.String

public data class Store(
    public val id: Long = 0,
    public val accountId: Long = 0,
    public val businessId: Long = 0,
    public val currency: String = "",
    public val productCount: Int = 0,
    public val publishedProductCount: Int = 0,
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val created: OffsetDateTime = OffsetDateTime.now(),
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val updated: OffsetDateTime = OffsetDateTime.now(),
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val deactivated: OffsetDateTime? = null,
    public val status: String = "",
    public val cancellationPolicy: CancellationPolicy = CancellationPolicy(),
    public val returnPolicy: ReturnPolicy = ReturnPolicy(),
)
