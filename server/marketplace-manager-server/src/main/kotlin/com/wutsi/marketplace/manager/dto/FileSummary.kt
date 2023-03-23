package com.wutsi.marketplace.manager.dto

import org.springframework.format.`annotation`.DateTimeFormat
import java.time.OffsetDateTime
import kotlin.Int
import kotlin.Long
import kotlin.String

public data class FileSummary(
    public val id: Long = 0,
    public val name: String = "",
    public val contentType: String = "",
    public val contentSize: Int = 0,
    public val url: String = "",
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val created: OffsetDateTime = OffsetDateTime.now(),
)
