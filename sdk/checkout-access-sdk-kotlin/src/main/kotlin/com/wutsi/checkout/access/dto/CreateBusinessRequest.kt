package com.wutsi.checkout.access.dto

import kotlin.Long
import kotlin.String

public data class CreateBusinessRequest(
    public val accountId: Long = 0,
    public val country: String = "",
    public val currency: String = "",
)
