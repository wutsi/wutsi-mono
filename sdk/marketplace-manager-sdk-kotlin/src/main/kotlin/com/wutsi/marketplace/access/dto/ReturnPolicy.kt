package com.wutsi.marketplace.access.dto

import kotlin.Boolean
import kotlin.Int
import kotlin.String

public data class ReturnPolicy(
    public val accepted: Boolean = false,
    public val contactWindow: Int = 0,
    public val shipBackWindow: Int = 0,
    public val message: String? = null,
)
