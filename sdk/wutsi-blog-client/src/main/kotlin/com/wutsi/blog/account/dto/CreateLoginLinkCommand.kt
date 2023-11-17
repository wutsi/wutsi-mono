package com.wutsi.blog.account.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class CreateLoginLinkCommand(
    @get:Email @get:NotBlank val email: String? = null,
    val redirectUrl: String? = null,
)
