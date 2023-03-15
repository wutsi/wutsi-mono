package com.wutsi.platform.core.security.spring

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class AnonymousAuthentication : Authentication {
    override fun getName(): String = "anonymous"

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()

    override fun getCredentials(): Any = ""

    override fun getDetails(): Any = ""

    override fun getPrincipal(): Any = this

    override fun isAuthenticated(): Boolean = false

    override fun setAuthenticated(authenticated: Boolean) {
    }
}
