package com.wutsi.marketplace.access.dto

import jakarta.validation.constraints.NotBlank
import kotlin.String

public data class UpdateReservationStatusRequest(
    @get:NotBlank
    public val status: String = "",
)
