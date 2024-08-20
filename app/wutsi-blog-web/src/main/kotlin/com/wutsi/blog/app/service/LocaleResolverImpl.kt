package com.wutsi.blog.app.service

import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.CookieName
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.LocaleResolver
import java.util.Locale

class LocaleResolverImpl(
    private val requestContext: RequestContext,
) : LocaleResolver {
    override fun resolveLocale(request: HttpServletRequest): Locale {
        return resolveFromCookie()
            ?: resolveFromUser(requestContext.currentUser())
            ?: resolveFromHeader(request)
    }

    private fun resolveFromCookie(): Locale? {
        val value = CookieHelper.get(CookieName.LOCALE, requestContext.request)
        return if (value.isNullOrEmpty()) null else Locale(value)
    }

    private fun resolveFromUser(user: UserModel?): Locale? =
        user?.locale

    private fun resolveFromHeader(request: HttpServletRequest): Locale =
        request.locale

    override fun setLocale(request: HttpServletRequest, response: HttpServletResponse?, locale: Locale?) {
        CookieHelper.put(CookieName.LOCALE, locale.toString(), request, response)
    }
}
