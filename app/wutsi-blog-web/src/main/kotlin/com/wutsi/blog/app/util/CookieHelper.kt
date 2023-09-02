package com.wutsi.blog.app.util

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

object CookieHelper {
    const val ONE_DAY_SECONDS = 86400
    const val ONE_HOUR_SECONDS = 3600

    fun get(name: String, request: HttpServletRequest): String? =
        getCookie(name, request)?.value

    fun remove(name: String, response: HttpServletResponse) {
        val cookie = Cookie(name, "")
        cookie.maxAge = -1
        cookie.path = "/"
        response.addCookie(cookie)
    }

    fun put(
        name: String,
        value: String?,
        request: HttpServletRequest,
        response: HttpServletResponse?,
        maxAge: Int = ONE_DAY_SECONDS,
    ) {
        var cookie = getCookie(name, request)
        if (cookie == null) {
            cookie = Cookie(name, value)
        }

        cookie.value = value
        cookie.maxAge = maxAge
        cookie.path = "/"
        response?.addCookie(cookie)
    }

    private fun getCookie(name: String, request: HttpServletRequest): Cookie? {
        val cookies = request.cookies
        if (cookies == null || cookies.isEmpty()) {
            return null
        }
        return cookies.find { it.name == name }
    }
}
