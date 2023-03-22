package com.wutsi.checkout.manager.dto

import kotlin.Long
import kotlin.String

public data class AddPaymentMethodRequest(
    public val providerId: Long = 0,
    public val type: String = "",
    public val number: String = "",
    public val country: String = "",
    public val ownerName: String = "",
)
