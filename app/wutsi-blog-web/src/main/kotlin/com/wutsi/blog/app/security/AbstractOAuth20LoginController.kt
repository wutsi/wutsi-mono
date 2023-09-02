package com.wutsi.blog.app.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Response
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth20Service
import com.wutsi.blog.app.security.oauth.OAuthUser
import com.wutsi.platform.core.logging.KVLogger
import jakarta.servlet.http.HttpServletRequest

abstract class AbstractOAuth20LoginController(
    logger: KVLogger,
    objectMapper: ObjectMapper,
) : AbstractOAuthLoginController(logger, objectMapper) {
    protected abstract fun getOAuthService(): OAuth20Service

    protected abstract fun getUserUrl(): String

    override fun getAuthorizationUrl(request: HttpServletRequest): String {
        return getOAuthService().getAuthorizationUrl()
    }

    override fun getSigninUrl(request: HttpServletRequest): String {
        val code = request.getParameter("code")
        val accessToken = getOAuthService().getAccessToken(code).accessToken
        val user = toOAuthUser(accessToken)

        return getSigninUrl(accessToken, user)
    }

    override fun getError(request: HttpServletRequest) = request.getParameter("error")

    protected fun toOAuthUser(accessToken: String): OAuthUser {
        val response = fetchUser(accessToken)
        logger.add("OAuthUser", response.body)

        val attrs = objectMapper.readValue(response.body, Map::class.java) as Map<String, Any>
        return toOAuthUser(attrs)
    }

    private fun fetchUser(accessToken: String): Response {
        val request = OAuthRequest(Verb.GET, getUserUrl())
        val oauth = getOAuthService()
        oauth.signRequest(accessToken, request)

        return oauth.execute(request)
    }
}
