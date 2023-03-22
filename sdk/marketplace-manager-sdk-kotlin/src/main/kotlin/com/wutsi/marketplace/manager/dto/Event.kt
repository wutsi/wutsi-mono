package com.wutsi.marketplace.manager.dto

import java.time.OffsetDateTime
import kotlin.Boolean
import kotlin.String

public data class Event(
    public val online: Boolean = false,
    public val meetingId: String = "",
    public val meetingPassword: String? = null,
    public val meetingProvider: MeetingProviderSummary? = null,
    public val meetingJoinUrl: String? = null,
    public val starts: OffsetDateTime? = null,
    public val ends: OffsetDateTime? = null,
)
