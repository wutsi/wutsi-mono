package com.wutsi.application.membership.onboard.dto

import javax.validation.constraints.NotEmpty

data class VerifyPhoneRequest(
    @NotEmpty val code: String = "",
)
