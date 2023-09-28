package com.wutsi.blog.app.security.service

import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.security.oauth.OAuthTokenAuthentication
import com.wutsi.blog.app.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.savedrequest.RequestCache

class AuthenticationSuccessHandlerImpl(
    private val userService: UserService,
) : AuthenticationSuccessHandler {
    companion object {
        const val SESSION_ATTRIBUTE_REDIRECT_URL = "com.wutsi.redirect_url_key"
        private val LOGGER = LoggerFactory.getLogger(AuthenticationSuccessHandler::class.java)
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
                    val user = getUser(authentication)
                    return if (user == null) response.sendRedirect("/") else response.sendRedirect(user.slug)
                }
            } else {
                response.sendRedirect(redirectUrl.toString())
            }
        } finally {
            request.session.removeAttribute(SESSION_ATTRIBUTE_REDIRECT_URL)
        }
    }

    private fun getUser(authentication: Authentication): UserModel? {
        if (!(authentication is OAuthTokenAuthentication)) {
            return null
        }
        val accessToken = authentication.accessToken
        if (accessToken != null) {
            try {
                return userService.getByAccessToken(accessToken)
            } catch (ex: Exception) {
                LOGGER.error("Unable to resolve user from accessToken=$accessToken", ex)
            }
        }
        return null
    }
}
