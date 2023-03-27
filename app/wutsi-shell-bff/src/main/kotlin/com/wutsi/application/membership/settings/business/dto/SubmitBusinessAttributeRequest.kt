package com.wutsi.application.membership.settings.business.dto

import javax.validation.constraints.NotEmpty

data class SubmitBusinessAttributeRequest(
    @NotEmpty val value: String = "",
)
