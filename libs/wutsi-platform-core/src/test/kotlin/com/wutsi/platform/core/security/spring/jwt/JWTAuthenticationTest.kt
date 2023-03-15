package com.wutsi.platform.core.security.spring.jwt

import com.auth0.jwt.interfaces.DecodedJWT
import com.wutsi.platform.core.security.SubjectType.USER
import com.wutsi.platform.core.security.WutsiPrincipal
import com.wutsi.platform.core.test.TestRSAKeyProvider
import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class JWTAuthenticationTest {
    @Test
    fun test() {
        val auth = JWTAuthentication.of(createAccessToken())
        auth.setAuthenticated(true)

        assertEquals("12345", auth.name)
        assertEquals(mutableListOf(SimpleGrantedAuthority("a"), SimpleGrantedAuthority("b")), auth.authorities)
        assertTrue(auth.isAuthenticated)
        assertTrue(auth.details is DecodedJWT)

        assertTrue(auth.principal is WutsiPrincipal)
        val principal = auth.principal as WutsiPrincipal
        assertEquals("12345", principal.id)
        assertEquals("Ray Sponsible", principal.name)
        assertTrue(principal.admin)
        assertEquals(USER, principal.type)
    }

    private fun createAccessToken(): String =
        JWTBuilder(
            ttl = 100000,
            admin = true,
            keyProvider = TestRSAKeyProvider(),
            scope = listOf("a", "b"),
            subject = "12345",
            name = "Ray Sponsible",
            subjectType = USER,
        ).build()
}
