package com.wutsi.platform.core.security.servlet

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

internal class CorsFilterTest {
    lateinit var request: HttpServletRequest
    lateinit var response: HttpServletResponse
    lateinit var chain: FilterChain
    lateinit var filter: CorsFilter

    @BeforeEach
    fun setUp() {
        request = mock()
        response = mock()
        chain = mock()

        filter = CorsFilter()
    }

    @Test
    fun doFilter() {
        filter.doFilter(request, response, chain)

        verify(response).addHeader("Access-Control-Allow-Origin", "*")
        verify(response).addHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST, DELETE")
        verify(response).addHeader("Access-Control-Allow-Headers", "*")
        verify(response).addHeader("Access-Control-Expose-Headers", "*")
        verify(chain).doFilter(request, response)
    }
}
