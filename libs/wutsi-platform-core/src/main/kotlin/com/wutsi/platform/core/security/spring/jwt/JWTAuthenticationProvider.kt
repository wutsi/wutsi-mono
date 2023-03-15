package com.wutsi.platform.core.security.spring.jwt

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication

class JWTAuthenticationProvider : AuthenticationProvider {
    override fun authenticate(auth: Authentication): Authentication {
        auth.isAuthenticated = true
        return auth
    }

    override fun supports(clazz: Class<*>) = JWTAuthentication::class.java == clazz
}
