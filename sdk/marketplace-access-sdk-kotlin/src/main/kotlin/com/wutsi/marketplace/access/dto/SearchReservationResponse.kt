package com.wutsi.marketplace.access.dto

import kotlin.collections.List

public data class SearchReservationResponse(
    public val reservations: List<ReservationSummary> = emptyList(),
)
