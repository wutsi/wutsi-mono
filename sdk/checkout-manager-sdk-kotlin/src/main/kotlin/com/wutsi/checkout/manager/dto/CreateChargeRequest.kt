package com.wutsi.checkout.manager.dto

import kotlin.Long
import kotlin.String

public data class CreateChargeRequest(
    public val email: String = "",
    public val paymentMethodType: String? = null,
    public val paymentMethodOwnerName: String? = null,
    public val paymentProviderId: Long? = null,
    public val paymentMethodToken: String? = null,
    public val paymenMethodNumber: String? = null,
    public val businessId: Long = 0,
    public val orderId: String = "",
    public val description: String? = null,
    public val idempotencyKey: String = "",
)
