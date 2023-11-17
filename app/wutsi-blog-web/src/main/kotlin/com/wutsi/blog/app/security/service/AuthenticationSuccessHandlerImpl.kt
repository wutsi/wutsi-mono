package com.wutsi.blog.app.security.service

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.savedrequest.RequestCache

class AuthenticationSuccessHandlerImpl : AuthenticationSuccessHandler {
    companion object {
        const val SESSION_ATTRIBUTE_REDIRECT_URL = "com.wutsi.redirect_url_key"
    }

    private val requestCache: RequestCache = HttpSessionRequestCache()

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        if (response.isCommitted) {
            return
        }

        val redirectUrl = request.session.getAttribute(SESSION_ATTRIBUTE_REDIRECT_URL)
        try {
            if (redirectUrl == null) {
                val savedRequest = this.requestCache.getRequest(request, response)
                if (savedRequest != null) {
                    response.sendRedirect(savedRequest.redirectUrl)
                } else {
                    return response.sendRedirect("/")
                }
            } else {
                response.sendRedirect(redirectUrl.toString())
            }
        } finally {
            request.session.removeAttribute(SESSION_ATTRIBUTE_REDIRECT_URL)
        }
    }
}
