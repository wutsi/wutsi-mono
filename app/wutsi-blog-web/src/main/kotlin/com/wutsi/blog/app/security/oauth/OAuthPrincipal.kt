package com.wutsi.blog.app.security.oauth

import java.security.Principal

class OAuthPrincipal(
    val accessToken: String,
    val user: OAuthUser,
) : Principal {
    override fun getName() = accessToken
}
