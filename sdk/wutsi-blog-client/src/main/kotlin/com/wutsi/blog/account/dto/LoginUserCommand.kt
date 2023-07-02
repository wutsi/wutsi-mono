package com.wutsi.blog.account.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

data class LoginUserCommand(
    @get:NotEmpty val accessToken: String = "",
    @get:NotEmpty val provider: String = "",
    @get:NotEmpty val providerUserId: String = "",
    @get:NotEmpty val fullName: String = "",
    @get:Email val email: String? = null,
    val pictureUrl: String? = null,
    val refreshToken: String? = null,
    val language: String? = null,
    val country: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
