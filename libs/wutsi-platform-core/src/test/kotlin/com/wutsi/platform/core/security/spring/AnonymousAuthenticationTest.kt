package com.wutsi.platform.core.security.spring

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class AnonymousAuthenticationTest {
    @Test
    fun test() {
        val auth = AnonymousAuthentication()
        auth.setAuthenticated(true)

        assertEquals("anonymous", auth.name)
        assertTrue(auth.authorities.isEmpty())
        assertFalse(auth.isAuthenticated)
    }
}
