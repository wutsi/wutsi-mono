package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.page.login.model.SessionModel
import com.wutsi.blog.app.page.login.service.AccessTokenStorage
import com.wutsi.blog.app.page.login.service.SessionMapper
import com.wutsi.platform.core.error.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class CurrentSessionHolder(
    private val authBackend: AuthenticationBackend,
    private val tokenStorage: AccessTokenStorage,
    private val sessionMapper: SessionMapper,
    private val request: HttpServletRequest,
    private val response: HttpServletResponse,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CurrentSessionHolder::class.java)
    }

    private var session: SessionModel? = null

    fun session(): SessionModel? {
        if (session != null) {
            return session
        }

        val token = accessToken()
        if (token.isNullOrEmpty()) {
            return null
        }

        try {
            session = sessionMapper.toSessionModel(
                authBackend.session(token).session,
            )
        } catch (e: Exception) {
            LOGGER.warn("Unable to resolve user associate with access_token $token", e)
            if (e is NotFoundException) {
                tokenStorage.delete(response)
            }
        }

        return session
    }

    fun accessToken(): String? {
        return tokenStorage.get(request)
    }
}
