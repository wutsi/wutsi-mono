package com.wutsi.application.membership.onboard.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty

data class SubmitPinRequest(
    @NotEmpty
    @Min(6)
    @Max(6)
    val pin: String = "",
)
