package com.wutsi.application.marketplace.settings.product.dto

import javax.validation.constraints.NotEmpty

data class SubmitAttributeRequest(
    @NotEmpty val value: String = "",
)
