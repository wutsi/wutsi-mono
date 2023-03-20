package com.wutsi.membership.access.dto

import kotlin.Long
import kotlin.String

public data class CreateAccountRequest(
    public val phoneNumber: String = "",
    public val language: String = "",
    public val country: String = "",
    public val displayName: String = "",
    public val pictureUrl: String? = null,
    public val cityId: Long? = null,
)
