package com.wutsi.blog.app.security.service

import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.security.oauth.OAuthTokenAuthentication
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.savedrequest.RequestCache
import java.lang.Exception
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthenticationSuccessHandlerImpl(
    private val userService: UserService,
) : AuthenticationSuccessHandler {
    companion object {
        const val REDIRECT_URL_KEY = "com.wutsi.redirect_url_key"
        private val LOGGER = LoggerFactory.getLogger(AuthenticationSuccessHandler::class.java)
    }

    private val requestCache: RequestCache = HttpSessionRequestCache()

    override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
        if (response.isCommitted()) {
            return
        }

        val redirectUrl = request.session.getAttribute(REDIRECT_URL_KEY)
        try {
            if (redirectUrl == null) {
                val savedRequest = this.requestCache.getRequest(request, response)
                if (savedRequest != null) {
                    response.sendRedirect(savedRequest.getRedirectUrl())
                } else {
                    val user = getUser(authentication)
                    return if (user == null) response.sendRedirect("/") else response.sendRedirect(user.slug)
                }
            } else {
                response.sendRedirect(redirectUrl.toString())
            }
        } finally {
            request.session.removeAttribute(REDIRECT_URL_KEY)
        }
    }

    private fun getUser(authentication: Authentication): UserModel? {
        if (!(authentication is OAuthTokenAuthentication)) {
            return null
        }
        val accessToken = authentication.accessToken
        try {
            return userService.getByAccessToken(accessToken)
        } catch (ex: Exception) {
            LOGGER.error("Unable to resolve user from accessToken=$accessToken", ex)
            return null
        }
    }
}
