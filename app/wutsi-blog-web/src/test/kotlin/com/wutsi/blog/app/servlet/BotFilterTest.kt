package com.wutsi.blog.app.servlet

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Test
import ua_parser.Client

class BotFilterTest {
    private val request = mock<HttpServletRequest> { }
    private val response = mock<HttpServletResponse> { }
    private val chain = mock<FilterChain> {}
    private val filter = BotFilter()

    @Test
    fun bot() {
        doReturn("Googlebot/2.1 (+http://www.google.com/bot.html)").whenever(request).getHeader("User-Agent")

        filter.doFilter(request, response, chain)

        verify(request).setAttribute(BotFilter.ATTRIBUTE_UA_BOT, true)
        verify(request).setAttribute(eq(BotFilter.ATTRIBUTE_UA_CLIENT), any<Client>())
        chain.doFilter(request, response)
    }

    @Test
    fun `not bot`() {
        doReturn("Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)")
            .whenever(request)
            .getHeader("User-Agent")

        filter.doFilter(request, response, chain)

        verify(request).setAttribute(BotFilter.ATTRIBUTE_UA_BOT, false)
        verify(request).setAttribute(eq(BotFilter.ATTRIBUTE_UA_CLIENT), any<Client>())
        chain.doFilter(request, response)
    }
}