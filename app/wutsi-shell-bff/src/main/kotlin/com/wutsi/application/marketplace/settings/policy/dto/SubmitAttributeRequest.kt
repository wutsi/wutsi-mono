package com.wutsi.application.marketplace.settings.policy.dto

import javax.validation.constraints.NotEmpty

data class SubmitAttributeRequest(
    @NotEmpty val value: String = "",
)
