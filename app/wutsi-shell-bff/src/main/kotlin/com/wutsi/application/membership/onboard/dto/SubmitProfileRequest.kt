package com.wutsi.application.membership.onboard.dto

import javax.validation.constraints.NotEmpty

data class SubmitProfileRequest(
    @NotEmpty val displayName: String = "",
)
