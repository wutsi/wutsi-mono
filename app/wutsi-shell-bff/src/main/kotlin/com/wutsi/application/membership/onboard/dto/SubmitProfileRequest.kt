package com.wutsi.application.membership.onboard.dto

import jakarta.validation.constraints.NotEmpty

data class SubmitProfileRequest(
    @NotEmpty val displayName: String = "",
)
