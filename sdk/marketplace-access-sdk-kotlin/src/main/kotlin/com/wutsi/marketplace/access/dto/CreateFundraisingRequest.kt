package com.wutsi.marketplace.access.dto

import kotlin.Long
import kotlin.String

public data class CreateFundraisingRequest(
    public val accountId: Long = 0,
    public val businessId: Long = 0,
    public val currency: String = "",
)
