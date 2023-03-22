package com.wutsi.checkout.manager.dto

import kotlin.String

public data class UpdateOrderStatusRequest(
    public val orderId: String = "",
    public val status: String = "",
    public val reason: String? = null,
)
