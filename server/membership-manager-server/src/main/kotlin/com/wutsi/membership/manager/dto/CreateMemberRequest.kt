package com.wutsi.membership.manager.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kotlin.Long
import kotlin.String

public data class CreateMemberRequest(
    @get:NotBlank
    @get:Size(max = 30)
    public val phoneNumber: String = "",
    @get:NotBlank
    public val displayName: String = "",
    @get:Size(
        min = 2,
        max = 2,
    )
    public val country: String = "",
    @get:NotBlank
    @get:Size(
        min = 4,
        max = 30,
    )
    public val pin: String = "",
    public val cityId: Long? = null,
)
