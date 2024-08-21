package com.wutsi.blog.app.service

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.util.CookieName
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.servlet.LocaleResolver
import java.util.Locale
import kotlin.test.assertEquals

class LocaleResolverImplTest {
    private lateinit var request: HttpServletRequest
    private lateinit var response: HttpServletResponse
    private lateinit var requestContext: RequestContext

    private lateinit var resolver: LocaleResolver

    @BeforeEach
    fun setUp() {
        request = mock<HttpServletRequest> {}
        response = mock<HttpServletResponse> {}

        requestContext = mock<RequestContext> {}
        doReturn(request).whenever(requestContext).request

        resolver = LocaleResolverImpl(requestContext)
    }

    @Test
    fun `resolve from parameter`() {
        doReturn("fr").whenever(request).getParameter(LocaleResolverImpl.PARAMETER)

        val result = resolver.resolveLocale(request)
        assertEquals(Locale("fr"), result)
    }

    @Test
    fun `resolve from cookies`() {
        val cookies = arrayOf(
            Cookie("foo", "xyz"),
            Cookie("foo", ""),
            Cookie(CookieName.LOCALE, "fr"),
        )
        doReturn(cookies).whenever(request).cookies

        val result = resolver.resolveLocale(request)
        assertEquals(Locale("fr"), result)
    }

    @Test
    fun `resolve from user`() {
        val user = UserModel(locale = Locale("fr"))
        doReturn(user).whenever(requestContext).currentUser()

        val result = resolver.resolveLocale(request)
        assertEquals(Locale("fr"), result)
    }

    @Test
    fun `resolve from header`() {
        doReturn(Locale("fr")).whenever(request).locale

        val result = resolver.resolveLocale(request)
        assertEquals(Locale("fr"), result)
    }
}
