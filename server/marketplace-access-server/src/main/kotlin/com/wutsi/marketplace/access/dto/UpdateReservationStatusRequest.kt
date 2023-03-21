package com.wutsi.marketplace.access.dto

import javax.validation.constraints.NotBlank
import kotlin.String

public data class UpdateReservationStatusRequest(
    @get:NotBlank
    public val status: String = "",
)
