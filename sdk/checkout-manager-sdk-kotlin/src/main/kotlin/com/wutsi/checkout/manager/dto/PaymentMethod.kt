package com.wutsi.checkout.manager.dto

import java.time.OffsetDateTime
import kotlin.Long
import kotlin.String

public data class PaymentMethod(
    public val accountId: Long = 0,
    public val token: String = "",
    public val type: String = "",
    public val ownerName: String = "",
    public val number: String = "",
    public val country: String = "",
    public val status: String = "",
    public val created: OffsetDateTime = OffsetDateTime.now(),
    public val updated: OffsetDateTime = OffsetDateTime.now(),
    public val deactivated: OffsetDateTime? = null,
    public val provider: PaymentProviderSummary = PaymentProviderSummary(),
)
