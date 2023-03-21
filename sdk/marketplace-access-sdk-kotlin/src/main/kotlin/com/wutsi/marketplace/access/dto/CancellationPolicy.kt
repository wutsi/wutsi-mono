package com.wutsi.marketplace.access.dto

import kotlin.Boolean
import kotlin.Int
import kotlin.String

public data class CancellationPolicy(
    public val accepted: Boolean = false,
    public val window: Int = 0,
    public val message: String? = null,
)
