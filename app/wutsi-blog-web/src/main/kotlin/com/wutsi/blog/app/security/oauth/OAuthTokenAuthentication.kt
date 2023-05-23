package com.wutsi.blog.app.security.oauth

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class OAuthTokenAuthentication(
    private val principal: OAuthPrincipal,
    val accessToken: String,
) : Authentication {
    private var authenticated = false

    override fun getName() = accessToken

    override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()

    override fun getCredentials() = null

    override fun getDetails() = null

    override fun getPrincipal() = principal

    override fun isAuthenticated(): Boolean {
        return authenticated
    }

    @Throws(IllegalArgumentException::class)
    override fun setAuthenticated(value: Boolean) {
        authenticated = value
    }
}
