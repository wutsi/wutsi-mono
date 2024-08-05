package com.wutsi.application.membership.settings.profile.dto

import jakarta.validation.constraints.NotEmpty

data class SubmitOTPRequest(
    @NotEmpty val code: String = "",
)
