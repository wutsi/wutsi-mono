package com.wutsi.membership.access.dto

import kotlin.Boolean
import kotlin.Long
import kotlin.String

public data class EnableBusinessRequest(
    public val displayName: String = "",
    public val categoryId: Long = 0,
    public val country: String = "",
    public val cityId: Long = 0,
    public val street: String? = null,
    public val biography: String? = null,
    public val email: String = "",
    public val whatsapp: Boolean = false,
)
