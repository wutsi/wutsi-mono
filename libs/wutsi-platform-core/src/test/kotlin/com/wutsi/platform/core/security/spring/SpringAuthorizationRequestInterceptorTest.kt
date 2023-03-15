package com.wutsi.platform.core.security.spring

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.security.TokenProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpResponse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class SpringAuthorizationRequestInterceptorTest {
    private lateinit var headers: HttpHeaders
    private lateinit var request: HttpRequest
    private lateinit var response: ClientHttpResponse
    private lateinit var tokenProvider: TokenProvider
    private lateinit var exec: ClientHttpRequestExecution
    private lateinit var interceptor: SpringAuthorizationRequestInterceptor

    @BeforeEach
    fun setUp() {
        headers = HttpHeaders()
        request = mock()
        doReturn(headers).whenever(request).headers

        response = mock()
        exec = mock()
        doReturn(response).whenever(exec).execute(any(), any())

        tokenProvider = mock()

        interceptor = SpringAuthorizationRequestInterceptor(tokenProvider)
    }

    @Test
    fun interceptWithToken() {
        doReturn("foo").whenever(tokenProvider).getToken()

        interceptor.intercept(request, ByteArray(10), exec)

        assertTrue(headers["Authorization"]!![0]!!.startsWith("Bearer foo"))
    }

    @Test
    fun interceptWithNoToken() {
        doReturn(null).whenever(tokenProvider).getToken()

        interceptor.intercept(request, ByteArray(10), exec)

        assertNull(headers["Authorization"])
    }
}
