package com.wutsi.application.membership.onboard.dto

import javax.validation.constraints.NotEmpty

data class SubmitPhoneRequest(
    @NotEmpty val phoneNumber: String = "",
)
