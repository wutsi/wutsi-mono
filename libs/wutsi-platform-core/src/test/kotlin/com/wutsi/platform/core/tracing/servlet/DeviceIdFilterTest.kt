package com.wutsi.platform.core.tracing.servlet

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.tracing.DeviceIdProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify

class DeviceIdFilterTest {
    lateinit var request: HttpServletRequest
    lateinit var response: HttpServletResponse
    lateinit var chain: FilterChain
    lateinit var duid: DeviceIdProvider
    lateinit var filter: DeviceIdFilter

    @BeforeEach
    fun setUp() {
        request = mock()
        response = mock()
        chain = mock()
        duid = mock()

        filter = DeviceIdFilter(duid)
    }

    @Test
    fun `filter with device-ID available`() {
        doReturn("xxx").whenever(duid).get(request)

        filter.doFilter(request, response, chain)

        verify(duid).set("xxx", request, response)
        verify(chain).doFilter(request, response)
    }

    @Test
    fun `filter with no device-ID available`() {
        doReturn(null).whenever(duid).get(request)

        filter.doFilter(request, response, chain)

        verify(duid, never()).set(anyOrNull(), eq(request), eq(response))
        verify(chain).doFilter(request, response)
    }

    @Test
    fun `filter with error`() {
        doReturn("xxx").whenever(duid).get(request)
        doThrow(ServletException::class).whenever(chain).doFilter(request, response)

        try {
            filter.doFilter(request, response, chain)
        } catch (ex: Exception) {
        } finally {
            verify(duid).set("xxx", request, response)
            verify(chain).doFilter(request, response)
        }
    }
}
