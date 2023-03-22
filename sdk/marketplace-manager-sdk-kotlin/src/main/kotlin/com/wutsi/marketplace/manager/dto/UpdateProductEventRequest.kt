package com.wutsi.marketplace.manager.dto

import java.time.OffsetDateTime
import kotlin.Boolean
import kotlin.Long
import kotlin.String

public data class UpdateProductEventRequest(
    public val productId: Long = 0,
    public val online: Boolean = false,
    public val meetingId: String? = null,
    public val meetingPassword: String? = null,
    public val meetingProviderId: Long? = null,
    public val starts: OffsetDateTime? = null,
    public val ends: OffsetDateTime? = null,
)
