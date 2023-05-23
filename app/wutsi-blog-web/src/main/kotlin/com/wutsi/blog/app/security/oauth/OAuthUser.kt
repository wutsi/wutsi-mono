package com.wutsi.blog.app.security.oauth

data class OAuthUser(
    val id: String = "",
    val fullName: String = "",
    val email: String? = null,
    val pictureUrl: String? = null,
    val provider: String = "",
)
