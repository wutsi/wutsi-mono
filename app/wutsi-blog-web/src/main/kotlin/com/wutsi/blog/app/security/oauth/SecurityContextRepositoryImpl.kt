package com.wutsi.blog.app.security.oauth

import com.wutsi.blog.app.service.AccessTokenStorage
import com.wutsi.platform.core.security.spring.AnonymousAuthentication
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.TransientSecurityContext
import org.springframework.security.web.context.HttpRequestResponseHolder
import org.springframework.security.web.context.SecurityContextRepository

class SecurityContextRepositoryImpl(private val storage: AccessTokenStorage) : SecurityContextRepository {
    override fun containsContext(request: HttpServletRequest): Boolean =
        storage.get(request) != null

    override fun saveContext(context: SecurityContext, request: HttpServletRequest, response: HttpServletResponse) {
        val auth = context.authentication
        if (auth is OAuthTokenAuthentication) {
            storage.put(auth.accessToken, request, response)
        } else if (auth == null) {
            storage.delete(response)
        }
    }

    override fun loadContext(requestResponseHolder: HttpRequestResponseHolder): SecurityContext {
        val token = storage.get(requestResponseHolder.request)
        return if (token.isNullOrEmpty()) {
            TransientSecurityContext(AnonymousAuthentication())
        } else {
            val authentication = OAuthTokenAuthentication(OAuthPrincipal(token, OAuthUser()), token)
            authentication.isAuthenticated = true
            TransientSecurityContext(authentication)
        }
    }
}
