package com.wutsi.application.membership.onboard.dto

import jakarta.validation.constraints.NotEmpty

data class VerifyPhoneRequest(
    @NotEmpty val code: String = "",
)
