package com.wutsi.platform.core.tracing.servlet

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.tracing.DeviceIdProvider
import com.wutsi.platform.core.tracing.TracingContext
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class DeviceIdProviderHeaderTest {
    private lateinit var request: HttpServletRequest
    private lateinit var response: HttpServletResponse
    private lateinit var provider: DeviceIdProvider

    @BeforeEach
    fun setUp() {
        request = mock()
        response = mock()
        provider = DeviceIdProviderHeader()
    }

    @Test
    fun `return null when attribute not in header`() {
        doReturn(null).whenever(request).getHeader(TracingContext.HEADER_DEVICE_ID)

        val value = provider.get(request)
        assertNull(value)
    }

    @Test
    fun `return attribute in header`() {
        doReturn("foo").whenever(request).getHeader(TracingContext.HEADER_DEVICE_ID)

        val value = provider.get(request)
        assertEquals("foo", value)
    }

    @Test
    fun set() {
        provider.set("foo", request, response)
        verify(response).addHeader(TracingContext.HEADER_DEVICE_ID, "foo")
    }
}
