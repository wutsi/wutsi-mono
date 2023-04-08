package com.wutsi.marketplace.access.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import kotlin.Long
import kotlin.String

public data class CreateFundraisingRequest(
    public val accountId: Long = 0,
    public val businessId: Long = 0,
    @get:NotBlank
    @get:Size(
        min = 3,
        max = 3,
    )
    public val currency: String = "",
    public val amount: Long = 0,
)
