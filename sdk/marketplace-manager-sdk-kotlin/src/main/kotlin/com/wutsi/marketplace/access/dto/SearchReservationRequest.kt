package com.wutsi.marketplace.access.dto

import kotlin.Int
import kotlin.String

public data class SearchReservationRequest(
    public val orderId: String? = null,
    public val limit: Int = 20,
    public val offset: Int = 0,
)
