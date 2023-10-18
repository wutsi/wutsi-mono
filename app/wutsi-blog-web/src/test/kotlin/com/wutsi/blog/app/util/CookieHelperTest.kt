package com.wutsi.blog.app.util

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.model.UserModel
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CookieHelperTest {
    private lateinit var request: HttpServletRequest
    private lateinit var response: HttpServletResponse

    @BeforeEach
    fun setUp() {
        request = mock()
        response = mock()
    }

    @Test
    fun get() {
        doReturn(
            arrayOf(
                Cookie("a", "1"),
                Cookie("b", "2")
            )
        ).whenever(request).cookies

        assertEquals("1", CookieHelper.get("a", request))
        assertNull(CookieHelper.get("c", request))
    }

    @Test
    fun remove() {
        CookieHelper.remove("a", response)

        val cookie = argumentCaptor<Cookie>()
        verify(response).addCookie(cookie.capture())
        assertEquals("", cookie.firstValue.value)
        assertEquals(-1, cookie.firstValue.maxAge)
        assertEquals("a", cookie.firstValue.name)
        assertEquals("/", cookie.firstValue.path)
    }

    @Test
    fun putAddNew() {
        CookieHelper.put("a", "1", request, response, 10)

        val cookie = argumentCaptor<Cookie>()
        verify(response).addCookie(cookie.capture())
        assertEquals("1", cookie.firstValue.value)
        assertEquals(10, cookie.firstValue.maxAge)
        assertEquals("a", cookie.firstValue.name)
        assertEquals("/", cookie.firstValue.path)
    }

    @Test
    fun putUpdate() {
        doReturn(
            arrayOf(
                Cookie("a", "0000"),
            )
        ).whenever(request).cookies

        CookieHelper.put("a", "1", request, response, 10)

        val cookie = argumentCaptor<Cookie>()
        verify(response).addCookie(cookie.capture())
        assertEquals("1", cookie.firstValue.value)
        assertEquals(10, cookie.firstValue.maxAge)
        assertEquals("a", cookie.firstValue.name)
        assertEquals("/", cookie.firstValue.path)
    }

    @Test
    fun preSubscribeKey() {
        val blog = UserModel(1)
        assertEquals("_w_psb-u1", CookieHelper.preSubscribeKey(blog))
    }
}
