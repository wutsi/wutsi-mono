package com.wutsi.application.membership.login.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty

data class SubmitPasscodeRequest(
    @NotEmpty
    @Min(6)
    @Max(6)
    val pin: String = "",
)
