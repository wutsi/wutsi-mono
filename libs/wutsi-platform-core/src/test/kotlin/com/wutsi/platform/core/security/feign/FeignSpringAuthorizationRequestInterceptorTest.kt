package com.wutsi.platform.core.security.feign

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.security.TokenProvider
import feign.RequestTemplate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class FeignSpringAuthorizationRequestInterceptorTest {
    private lateinit var tokenProvider: TokenProvider
    private val request = RequestTemplate()
    private lateinit var interceptor: FeignAuthorizationRequestInterceptor

    @BeforeEach
    fun setUp() {
        tokenProvider = mock()
        interceptor = FeignAuthorizationRequestInterceptor(tokenProvider)
    }

    @Test
    fun `set authorization header when JWT available`() {
        doReturn("111").whenever(tokenProvider).getToken()
        interceptor.apply(request)

        assertTrue(request.headers()["Authorization"]!!.contains("Bearer 111"))
    }

    @Test
    fun `do not set authorization header when JWT not avaialble`() {
        doReturn(null).whenever(tokenProvider).getToken()
        interceptor.apply(request)

        assertFalse(request.headers().containsKey("Authorization"))
    }
}
