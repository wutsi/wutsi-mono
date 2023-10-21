package com.wutsi.blog.app.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.exceptions.OAuthException
import com.wutsi.blog.app.security.oauth.OAuthAuthenticationProvider
import com.wutsi.blog.app.security.service.AuthenticationSuccessHandlerImpl
import com.wutsi.platform.core.logging.KVLogger
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import java.net.URLEncoder

abstract class AbstractOAuthLoginController(
    logger: KVLogger,
    objectMapper: ObjectMapper,
) : AbstractLoginController(logger, objectMapper) {
    companion object {
        const val CONNECT_KEY = "com.wutsi.connect"
        const val REQUEST_TOKEN_KEY = "com.wutsi.request_token"
    }

    protected abstract fun getAuthorizationUrl(request: HttpServletRequest): String

    protected abstract fun getError(request: HttpServletRequest): String?

    protected abstract fun getSigninUrl(request: HttpServletRequest): String

    protected open fun getConnectUrl(request: HttpServletRequest): String {
        return getSigninUrl(request)
    }

    protected open fun cleanup(request: HttpServletRequest) {
        request.session.removeAttribute(CONNECT_KEY)
        request.session.removeAttribute(REQUEST_TOKEN_KEY)
    }

    @GetMapping()
    fun login(request: HttpServletRequest): String {
        val connect = request.getParameter("connect")
        if (connect != null) {
            request.session.setAttribute(CONNECT_KEY, connect)
        } else {
            request.session.removeAttribute(CONNECT_KEY)
        }

        val redirect = request.getParameter("redirect")
        if (redirect != null) {
            request.session.setAttribute(AuthenticationSuccessHandlerImpl.SESSION_ATTRIBUTE_REDIRECT_URL, redirect)
        } else {
            request.session.removeAttribute(AuthenticationSuccessHandlerImpl.SESSION_ATTRIBUTE_REDIRECT_URL)
        }

        val storyId = request.getParameter("story-id")
        if (storyId != null) {
            request.session.setAttribute(OAuthAuthenticationProvider.SESSION_ATTRIBUTE_STORY_ID, storyId)
        } else {
            request.session.removeAttribute(OAuthAuthenticationProvider.SESSION_ATTRIBUTE_STORY_ID)
        }

        val referer = request.getParameter("referer")
        if (referer != null) {
            request.session.setAttribute(OAuthAuthenticationProvider.SESSION_ATTRIBUTE_REFERER, referer)
        } else {
            request.session.removeAttribute(OAuthAuthenticationProvider.SESSION_ATTRIBUTE_REFERER)
        }

        val url = getAuthorizationUrl(request)
        return "redirect:$url"
    }

    @GetMapping("/callback")
    open fun callback(request: HttpServletRequest): String {
        var url: String
        try {
            val error = getError(request)
            val connect = request.session.getAttribute(CONNECT_KEY)
            if (connect == null) {
                url = if (error == null) getSigninUrl(request) else getErrorUrl(error, request)
            } else {
                url = if (error == null) getConnectUrl(request) else getErrorUrl(error, request)
            }

            logger.add("redirect_url", url)
            return "redirect:$url"
        } catch (ex: OAuthException) {
            url = getErrorUrl(ex.message, request)
            logger.add("Exception", ex.javaClass.name)
            logger.add("ExceptionMessage", ex.message)
            LoggerFactory.getLogger(javaClass).error("Failure", ex)
        } finally {
            cleanup(request)
        }

        logger.add("RedirectURL", url)
        return "redirect:$url"
    }

    private fun getErrorUrl(error: String?, request: HttpServletRequest): String {
        val connect = request.session.getAttribute(CONNECT_KEY)
        val err = if (error == null) "failed" else URLEncoder.encode(error, "utf-8")
        return if (connect == null) "/login?error=$err" else "/me/connect?error=$err"
    }
}
