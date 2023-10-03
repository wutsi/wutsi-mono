package com.wutsi.platform.core.tracing.spring

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.tracing.DeviceIdProvider
import com.wutsi.platform.core.tracing.TracingContext
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class RequestTracingContextTest {
    private lateinit var request: HttpServletRequest
    private lateinit var tc: TracingContext
    private lateinit var deviceIdProvider: DeviceIdProvider

    @BeforeEach
    fun setUp() {
        request = mock()
        deviceIdProvider = mock()
        tc = RequestTracingContext(request, deviceIdProvider)
    }

    @Test
    fun `return tenant-id from header`() {
        doReturn("111").whenever(request).getHeader(TracingContext.HEADER_TENANT_ID)
        assertEquals("111", tc.tenantId())
    }

    @Test
    fun `return client-info from header`() {
        doReturn("foo").whenever(request).getHeader(TracingContext.HEADER_CLIENT_INFO)
        assertEquals("foo", tc.clientInfo())
    }

    @Test
    fun `return trace-id from Header`() {
        doReturn("from-header").whenever(request).getHeader(TracingContext.HEADER_TRACE_ID)
        assertEquals("from-header", tc.traceId())
        verify(request).setAttribute(TracingContext.HEADER_TRACE_ID, "from-header")
    }

    @Test
    fun `return trace-id from Heroku Header`() {
        doReturn("from-heroku").whenever(request).getHeader(TracingContext.HEADER_HEROKU_REQUEST_ID)
        assertEquals("from-heroku", tc.traceId())
        verify(request).setAttribute(TracingContext.HEADER_TRACE_ID, "from-heroku")
    }

    @Test
    fun `return default trace-id`() {
        val traceId = tc.traceId()
        assertEquals(36, traceId.length)
        verify(request).setAttribute(TracingContext.HEADER_TRACE_ID, traceId)
    }

    @Test
    fun `return client-id from header`() {
        doReturn("from-header").whenever(request).getHeader(TracingContext.HEADER_CLIENT_ID)
        assertEquals("from-header", tc.clientId())
    }

    @Test
    fun `return device-id from deviceIdProvider`() {
        doReturn("device-id").whenever(deviceIdProvider).get(request)
        assertEquals("device-id", tc.deviceId())
    }

    @Test
    fun `return NONE as device-id when device id not avaialble`() {
        doReturn(null).whenever(deviceIdProvider).get(request)
        assertEquals(TracingContext.NONE, tc.deviceId())
    }
}
