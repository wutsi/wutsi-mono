package com.wutsi.application.membership.onboard.dto

import jakarta.validation.constraints.NotEmpty

data class SubmitPhoneRequest(
    @NotEmpty val phoneNumber: String = "",
)
