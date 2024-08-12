package com.wutsi.blog.app.servlet

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.util.CookieName
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracking.ChannelDetector
import com.wutsi.platform.core.tracking.ChannelType
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ChannelFilterTest {
    private val request = mock<HttpServletRequest> { }
    private val response = mock<HttpServletResponse> { }
    private val chain = mock<FilterChain> {}
    private val detector = mock<ChannelDetector> {}
    private val logger = mock<KVLogger> {}
    private val filter = ChannelFilter(detector, logger, "https://www.wutsi.com")

    @BeforeEach
    fun setUp() {
        doReturn("Googlebot/2.1 (+http://www.google.com/bot.html)").whenever(request).getHeader("User-Agent")
        doReturn(StringBuffer("https://www.wutsi.com/read/123")).whenever(request).requestURL
    }

    @Test
    fun nullReferer() {
        doReturn(null).whenever(request).getHeader("Referer")
        doReturn(ChannelType.WEB).whenever(detector).detect(any(), any(), any())

        filter.doFilter(request, response, chain)

        val cookie = argumentCaptor<Cookie>()
        verify(response).addCookie(cookie.capture())
        assertEquals(CookieName.CHANNEL, cookie.firstValue.name)
        assertEquals(ChannelType.WEB.name, cookie.firstValue.value)

        verify(chain).doFilter(request, response)
    }

    @Test
    fun emptyReferer() {
        doReturn("").whenever(request).getHeader("Referer")
        doReturn(ChannelType.APP).whenever(detector).detect(any(), any(), any())

        filter.doFilter(request, response, chain)

        val cookie = argumentCaptor<Cookie>()
        verify(response).addCookie(cookie.capture())
        assertEquals(CookieName.CHANNEL, cookie.firstValue.name)
        assertEquals(ChannelType.APP.name, cookie.firstValue.value)

        verify(chain).doFilter(request, response)
    }

    @Test
    fun internal() {
        doReturn("https://www.wutsi.com/1/putin-in-china").whenever(request).getHeader("Referer")

        filter.doFilter(request, response, chain)

        verify(response, never()).addCookie(any())

        verify(chain).doFilter(request, response)
    }

    @Test
    fun external() {
        doReturn("https://www.facebook.com").whenever(request).getHeader("Referer")
        doReturn(ChannelType.SOCIAL).whenever(detector).detect(any(), any(), any())

        filter.doFilter(request, response, chain)

        val cookie = argumentCaptor<Cookie>()
        verify(response).addCookie(cookie.capture())
        assertEquals(CookieName.CHANNEL, cookie.firstValue.name)
        assertEquals(ChannelType.SOCIAL.name, cookie.firstValue.value)

        verify(chain).doFilter(request, response)
    }
}
