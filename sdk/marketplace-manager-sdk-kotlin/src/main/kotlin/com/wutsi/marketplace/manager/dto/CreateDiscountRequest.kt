package com.wutsi.marketplace.manager.dto

import java.time.OffsetDateTime
import kotlin.Boolean
import kotlin.Int
import kotlin.String

public data class CreateDiscountRequest(
    public val name: String = "",
    public val type: String = "",
    public val rate: Int = 0,
    public val starts: OffsetDateTime? = null,
    public val ends: OffsetDateTime? = null,
    public val allProducts: Boolean = false,
)
