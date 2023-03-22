package com.wutsi.checkout.manager.dto

import kotlin.Boolean
import kotlin.Long
import kotlin.String

public data class CreateBusinessRequest(
    public val displayName: String = "",
    public val categoryId: Long = 0,
    public val cityId: Long = 0,
    public val biography: String? = null,
    public val whatsapp: Boolean = false,
    public val email: String = "",
)
