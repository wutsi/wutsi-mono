package com.wutsi.marketplace.access.dto

import kotlin.String
import kotlin.collections.List

public data class CreateReservationRequest(
    public val orderId: String = "",
    public val items: List<ReservationItem> = emptyList(),
)
