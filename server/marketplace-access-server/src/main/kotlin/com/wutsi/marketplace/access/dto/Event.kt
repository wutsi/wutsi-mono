package com.wutsi.marketplace.access.dto

import org.springframework.format.`annotation`.DateTimeFormat
import java.time.OffsetDateTime
import kotlin.Boolean
import kotlin.String

public data class Event(
    public val online: Boolean = false,
    public val meetingId: String = "",
    public val meetingPassword: String? = null,
    public val meetingProvider: MeetingProviderSummary? = null,
    public val meetingJoinUrl: String? = null,
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val starts: OffsetDateTime? = null,
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val ends: OffsetDateTime? = null,
)
