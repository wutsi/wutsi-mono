package com.wutsi.platform.core.security.spring

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.security.TokenProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.servlet.http.HttpServletRequest

internal class RequestTokenProviderTest {
    private lateinit var request: HttpServletRequest
    private lateinit var provider: TokenProvider

    @BeforeEach
    fun setUp() {
        request = mock()
        provider = RequestTokenProvider(request)
    }

    @Test
    fun `return the JWT from request header`() {
        doReturn("BeaReR 111").whenever(request).getHeader("Authorization")

        assertEquals("111", provider.getToken())
    }

    @Test
    fun `return null when request header malformed`() {
        doReturn("-- 111").whenever(request).getHeader("Authorization")

        kotlin.test.assertNull(provider.getToken())
    }

    @Test
    fun `return null when request header not available`() {
        doReturn(null).whenever(request).getHeader("Authorization")

        kotlin.test.assertNull(provider.getToken())
    }
}
