package com.wutsi.blog.app.service

import au.com.flyingkite.mobiledetect.UAgentInfo
import com.vladmihalcea.hibernate.util.LogUtils.LOGGER
import com.wutsi.blog.app.model.Permission
import com.wutsi.blog.app.model.StoreModel
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.security.service.SecurityManager
import com.wutsi.blog.error.ErrorCode
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ForbiddenException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.TracingContext
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import java.util.Locale

@Component
class RequestContext(
    private val sessionHolder: CurrentSessionHolder,
    private val userHolder: CurrentUserHolder,
    private val storeHolder: CurrentStoreHolder,
    private val togglesHolder: TogglesHolder,
    private val localization: LocalizationService,
    private val securityManager: SecurityManager,
    val tracingContext: TracingContext,
    private val logger: KVLogger,
    val request: HttpServletRequest,
    val response: HttpServletResponse,
) {
    fun remoteIp(): String {
        val ip = request.getHeader("X-FORWARDED-FOR")
        return if (ip.isNullOrEmpty()) {
            request.remoteAddr
        } else {
            ip
        }
    }

    fun isMobileUserAgent(): Boolean {
        val ua = toUAgentInfo()
        return ua.detectMobileQuick()
    }

    fun isWebview(): Boolean {
        val userAgent = request.getHeader("User-Agent")
        val ua = toUAgentInfo()

        return (ua.detectIos() && !userAgent.lowercase().contains("safari")) ||
            (ua.detectAndroid() && !userAgent.lowercase().contains("wv")) ||
            (userAgent.contains("FB_IAB")) || // Facebook In App Browser
            (userAgent.contains("FBAV")) // Facebook App Version
    }

    private fun toUAgentInfo() = UAgentInfo(
        request.getHeader("User-Agent"),
        request.getHeader("Accept"),
    )

    fun currentSuperUser(): UserModel? =
        userHolder.superUser()

    fun currentStore(): StoreModel? =
        storeHolder.store()

    fun currentUser(): UserModel? =
        userHolder.user()

    fun accessToken(): String? =
        sessionHolder.accessToken()

    fun toggles(): Toggles =
        togglesHolder.get()

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
            throw ForbiddenException(Error(ErrorCode.PERMISSION_DENIED))
        }
    }

    fun deviceId() = tracingContext.deviceId()
}
