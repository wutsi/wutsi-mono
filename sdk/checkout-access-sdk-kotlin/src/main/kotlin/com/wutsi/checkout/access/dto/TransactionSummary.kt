package com.wutsi.checkout.access.dto

import java.time.OffsetDateTime
import kotlin.Long
import kotlin.String

public data class TransactionSummary(
    public val id: String = "",
    public val customerAccountId: Long? = null,
    public val businessId: Long = 0,
    public val type: String = "",
    public val amount: Long = 0,
    public val fees: Long = 0,
    public val gatewayFees: Long = 0,
    public val net: Long = 0,
    public val currency: String = "",
    public val status: String = "",
    public val orderId: String? = null,
    public val created: OffsetDateTime = OffsetDateTime.now(),
    public val updated: OffsetDateTime = OffsetDateTime.now(),
    public val paymentMethod: PaymentMethodSummary = PaymentMethodSummary(),
)
