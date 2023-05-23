package com.wutsi.blog.app.page.login.service

import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.CookieName
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AccessTokenStorage {
    fun get(request: HttpServletRequest): String? = CookieHelper.get(CookieName.ACCESS_TOKEN, request)

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
