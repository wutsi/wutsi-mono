package com.wutsi.checkout.access.dto

import kotlin.Long
import kotlin.String

public data class CreatePaymentMethodRequest(
    public val accountId: Long = 0,
    public val providerId: Long = 0,
    public val type: String = "",
    public val number: String = "",
    public val country: String = "",
    public val ownerName: String = "",
)
