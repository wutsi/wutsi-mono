package com.wutsi.blog.app.service

import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.CookieName
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class AccessTokenStorage {
    fun get(request: HttpServletRequest): String? =
        CookieHelper.get(CookieName.ACCESS_TOKEN, request)

    fun put(accessToken: String, request: HttpServletRequest, response: HttpServletResponse) {
        CookieHelper.put(
            name = CookieName.ACCESS_TOKEN,
            value = accessToken,
            request = request,
            response = response,
            maxAge = 30 * CookieHelper.ONE_DAY_SECONDS,
        )
    }

    fun delete(response: HttpServletResponse) {
        CookieHelper.remove(CookieName.ACCESS_TOKEN, response)
    }
}
