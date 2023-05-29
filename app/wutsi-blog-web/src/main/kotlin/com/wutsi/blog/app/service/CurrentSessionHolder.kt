package com.wutsi.blog.app.service

import au.com.flyingkite.mobiledetect.UAgentInfo
import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.backend.UserBackend
import com.wutsi.blog.app.common.service.LocalizationService
import com.wutsi.blog.app.common.service.Toggles
import com.wutsi.blog.app.common.service.TogglesHolder
import com.wutsi.blog.app.mapper.UserMapper
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.login.model.SessionModel
import com.wutsi.blog.app.page.login.service.AccessTokenStorage
import com.wutsi.blog.app.page.login.service.SessionMapper
import com.wutsi.blog.app.security.model.Permission
import com.wutsi.blog.app.security.service.SecurityManager
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ForbiddenException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.TracingContext
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component
import java.util.Locale
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class SessionContext(
    private val mapper: UserMapper,
    private val authBackend: AuthenticationBackend,
    private val userBackend: UserBackend,
    private val togglesHolder: TogglesHolder,
    private val tokenStorage: AccessTokenStorage,
    private val localization: LocalizationService,
    private val securityManager: SecurityManager,
    private val sessionMapper: SessionMapper,
    private val trackingContext: TracingContext,
    private val logger: KVLogger,
    val request: HttpServletRequest,
    val response: HttpServletResponse,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SessionContext::class.java)
    }

    private var session: SessionModel? = null

    fun currentSession(): SessionModel? {
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

    fun toggles(): Toggles =
        togglesHolder.get()

    fun accessToken(): String? {
        return tokenStorage.get(request)
    }
}
