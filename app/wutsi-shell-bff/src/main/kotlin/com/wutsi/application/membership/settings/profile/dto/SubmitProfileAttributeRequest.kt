package com.wutsi.application.membership.settings.profile.dto

import javax.validation.constraints.NotEmpty

data class SubmitProfileAttributeRequest(
    @NotEmpty val value: String = "",
)
