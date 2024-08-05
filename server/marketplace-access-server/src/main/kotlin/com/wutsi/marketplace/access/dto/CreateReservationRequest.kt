package com.wutsi.marketplace.access.dto

import jakarta.validation.constraints.NotBlank
import kotlin.String
import kotlin.collections.List

public data class CreateReservationRequest(
    @get:NotBlank
    public val orderId: String = "",
    public val items: List<ReservationItem> = emptyList(),
)
