package com.wutsi.platform.core.util.spring

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpResponse

internal class SpringContentTypeRequestInterceptorTest {
    @Test
    fun intercept() {
        val headers = HttpHeaders()
        val request = mock<HttpRequest>()
        doReturn(headers).whenever(request).headers

        val response = mock<ClientHttpResponse>()
        val exec = mock<ClientHttpRequestExecution>()
        doReturn(response).whenever(exec).execute(any(), any())

        val interceptor = SpringContentTypeRequestInterceptor("application/json")
        interceptor.intercept(request, ByteArray(10), exec)

        assertEquals("application/json", headers[HttpHeaders.CONTENT_TYPE]!![0])
    }
}
