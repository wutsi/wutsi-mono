package com.wutsi.membership.manager.dto

import kotlin.Long
import kotlin.String

public data class CreateMemberRequest(
    public val phoneNumber: String = "",
    public val displayName: String = "",
    public val country: String = "",
    public val pin: String = "",
    public val cityId: Long? = null,
)
