package com.wutsi.blog.client.user

import javax.validation.constraints.Email
import javax.validation.constraints.NotNull

data class AuthenticateRequest(
    @get:NotNull val siteId: Long? = null,
    @get:NotNull val provider: String? = null,
    @get:NotNull val providerUserId: String? = null,
    @get:NotNull val fullName: String? = null,
    @get:Email val email: String? = null,
    val pictureUrl: String? = null,
    @get:NotNull val accessToken: String? = null,
    val refreshToken: String? = null,
    val language: String? = null,
)
