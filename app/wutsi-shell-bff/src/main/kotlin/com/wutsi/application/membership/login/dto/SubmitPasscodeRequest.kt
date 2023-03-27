package com.wutsi.application.membership.login.dto

import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty

data class SubmitPasscodeRequest(
    @NotEmpty
    @Min(6)
    @Max(6)
    val pin: String = "",
)
