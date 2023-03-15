package com.wutsi.platform.core.security.spring.jwt

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test
import org.springframework.security.core.Authentication
import kotlin.test.assertTrue

internal class JWTAuthenticationProviderTest {
    val provider = JWTAuthenticationProvider()

    @Test
    fun testAuthenticate() {
        val auth = mock<Authentication>()
        provider.authenticate(auth)
        verify(auth).setAuthenticated(true)
    }

    @Test
    fun support() {
        assertTrue(provider.supports(JWTAuthentication::class.java))
    }
}
