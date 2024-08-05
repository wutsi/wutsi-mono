package com.wutsi.blog.account.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

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
    val ip: String? = null,
    val storyId: Long? = null,
    val referer: String? = null,
)
