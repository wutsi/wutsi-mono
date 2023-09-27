package com.wutsi.platform.core.tracing.feign

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.tracing.TracingContext
import feign.RequestTemplate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FeignTracingRequestInterceptorTest {
    private val clientId: String = "foo"
    private lateinit var tracingContext: TracingContext

    @BeforeEach
    fun setUp() {
        tracingContext = mock()

        doReturn("device-id").whenever(tracingContext).deviceId()
        doReturn("tenant-id").whenever(tracingContext).tenantId()
        doReturn("client-info").whenever(tracingContext).clientInfo()
        doReturn("trace-id").whenever(tracingContext).traceId()
    }

    @Test
    fun apply() {
        // WHEN
        val interceptor = FeignTracingRequestInterceptor(clientId, tracingContext)
        val template = mock<RequestTemplate>()
        interceptor.apply(template)

        // THEN
        verify(template).header(TracingContext.HEADER_CLIENT_ID, clientId)
        verify(template).header(TracingContext.HEADER_DEVICE_ID, "device-id")
        verify(template).header(TracingContext.HEADER_TRACE_ID, "trace-id")
        verify(template).header(TracingContext.HEADER_TENANT_ID, "tenant-id")
        verify(template).header(TracingContext.HEADER_CLIENT_INFO, "client-info")
        verify(template).header(TracingContext.HEADER_HEROKU_REQUEST_ID, "trace-id")
    }

    @Test
    fun `do not set tenant-id is not available`() {
        // GIVEN
        doReturn(null).whenever(tracingContext).tenantId()

        // WHEN
        val interceptor = FeignTracingRequestInterceptor(clientId, tracingContext)
        val template = mock<RequestTemplate>()
        interceptor.apply(template)

        // THEN
        verify(template, never()).header(eq(TracingContext.HEADER_TENANT_ID), any<String>())
    }

    @Test
    fun `do not set client-info is not available`() {
        // GIVEN
        doReturn(null).whenever(tracingContext).clientInfo()

        // WHEN
        val interceptor = FeignTracingRequestInterceptor(clientId, tracingContext)
        val template = mock<RequestTemplate>()
        interceptor.apply(template)

        // THEN
        verify(template, never()).header(eq(TracingContext.HEADER_CLIENT_INFO), any<String>())
    }
}
