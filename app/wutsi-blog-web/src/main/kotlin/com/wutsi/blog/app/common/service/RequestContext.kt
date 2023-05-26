package com.wutsi.blog.app.common.service

import au.com.flyingkite.mobiledetect.UAgentInfo
import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.backend.UserBackend
import com.wutsi.blog.app.page.login.model.SessionModel
import com.wutsi.blog.app.page.login.service.AccessTokenStorage
import com.wutsi.blog.app.page.login.service.SessionMapper
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserMapper
import com.wutsi.blog.app.model.StoryModel
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
class RequestContext(
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
        private val LOGGER = LoggerFactory.getLogger(RequestContext::class.java)
    }

    private var user: UserModel? = null
    private var superUser: UserModel? = null
    private var session: SessionModel? = null
    private var mobileUserAgent: Boolean? = null

    fun isMobileUserAgent(): Boolean {
        if (mobileUserAgent == null) {
            val ua = UAgentInfo(
                request.getHeader("User-Agent"),
                request.getHeader("Accept"),
            )
            mobileUserAgent = ua.detectMobileQuick()
        }
        return mobileUserAgent!!
    }

    fun deviceId(): String = trackingContext.deviceId()

    fun currentSuperUser(): UserModel? {
        if (superUser != null) {
            return superUser
        }

        val user = currentUser()
        if (user != null && user.superUser) {
            superUser = user
        } else {
            val session = currentSession()
                ?: return null

            if (session.runAsUserId != null) {
                superUser = mapper.toUserModel(
                    userBackend.get(session.runAsUserId).user,
                )
            }
        }

        return superUser
    }

    fun currentUser(): UserModel? {
        if (user != null) {
            return user
        }

        val session = currentSession()
            ?: return null

        try {
            if (session.runAsUserId != null) {
                user = mapper.toUserModel(
                    userBackend.get(session.runAsUserId).user,
                )
            } else {
                user = mapper.toUserModel(
                    userBackend.get(session.userId).user,
                )
            }
        } catch (e: Exception) {
            LOGGER.warn("Unable to resolve user ${session.userId}", e)
            if (e is NotFoundException) {
                tokenStorage.delete(response)
            }
        }

        return user
    }

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
//            LOGGER.warn("Unable to resolve user associate with access_token $token", e)
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

    fun languages(): List<Locale> =
        listOf(Locale.FRENCH, Locale.ENGLISH)

    fun supportsLanguage(language: String): Boolean =
        languages().find { it.language == language } != null

    fun getMessage(key: String, defaultKey: String? = null, args: Array<Any>? = null, locale: Locale? = null): String {
        try {
            return localization.getMessage(key, args, locale)
        } catch (ex: Exception) {
            if (defaultKey != null) {
                try {
                    return localization.getMessage(defaultKey)
                } catch (ex2: Exception) {
                }
            }
            return key
        }
    }

    fun checkAccess(story: StoryModel, requiredPermissions: List<Permission>) {
        val permissions = securityManager.permissions(story, currentUser())

        logger.add("PermissionsUser", permissions)
        logger.add("PermissionsExpected", requiredPermissions)
        if (!permissions.containsAll(requiredPermissions)) {
            LOGGER.error("required-permissions=$requiredPermissions - permissions=$permissions")
            throw ForbiddenException(Error("permission_error"))
        }
    }

    fun siteId(): Long = 1L
}
