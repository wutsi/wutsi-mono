package com.wutsi.platform.core.tracing.spring

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.tracing.TracingContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpResponse

internal class SpringTracingRequestInterceptorTest {
    @Test
    fun intercept() {
        val headers = HttpHeaders()
        val request = mock<HttpRequest>()
        doReturn(headers).whenever(request).headers

        val response = mock<ClientHttpResponse>()
        val exec = mock<ClientHttpRequestExecution>()
        doReturn(response).whenever(exec).execute(any(), any())

        val tc = mock<TracingContext>()
        doReturn("device-id").whenever(tc).deviceId()
        doReturn("trace-id").whenever(tc).traceId()
        doReturn("client-id").whenever(tc).clientId()
        doReturn("client-info").whenever(tc).clientInfo()
        doReturn("1").whenever(tc).tenantId()

        val interceptor = SpringTracingRequestInterceptor(tc)
        interceptor.intercept(request, ByteArray(10), exec)

        assertEquals("trace-id", headers[TracingContext.HEADER_TRACE_ID]!![0])
        assertEquals("client-id", headers[TracingContext.HEADER_CLIENT_ID]!![0])
        assertEquals("device-id", headers[TracingContext.HEADER_DEVICE_ID]!![0])
        assertEquals("1", headers[TracingContext.HEADER_TENANT_ID]!![0])
        assertEquals("client-info", headers[TracingContext.HEADER_CLIENT_INFO]!![0])
    }
}
