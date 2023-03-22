package com.wutsi.checkout.manager.dto

import kotlin.Long
import kotlin.String

public data class Discount(
    public val discountId: Long = 0,
    public val name: String = "",
    public val type: String = "",
    public val amount: Long = 0,
)
