package com.wutsi.marketplace.manager.dto

import org.springframework.format.`annotation`.DateTimeFormat
import java.time.OffsetDateTime
import javax.validation.constraints.NotNull
import kotlin.Boolean
import kotlin.Long
import kotlin.String

public data class UpdateProductEventRequest(
    public val productId: Long = 0,
    public val online: Boolean = false,
    public val meetingId: String? = null,
    public val meetingPassword: String? = null,
    public val meetingProviderId: Long? = null,
    @get:NotNull
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val starts: OffsetDateTime? = null,
    @get:NotNull
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val ends: OffsetDateTime? = null,
)
