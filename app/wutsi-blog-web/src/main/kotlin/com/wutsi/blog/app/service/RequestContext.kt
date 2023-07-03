package com.wutsi.blog.app.service

import au.com.flyingkite.mobiledetect.UAgentInfo
import com.vladmihalcea.hibernate.util.LogUtils.LOGGER
import com.wutsi.blog.app.model.Permission
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.security.service.SecurityManager
import com.wutsi.blog.error.ErrorCode
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ForbiddenException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.stereotype.Component
import java.util.Locale
import javax.servlet.http.HttpServletRequest

@Component
class RequestContext(
    private val sessionHolder: CurrentSessionHolder,
    private val userHolder: CurrentUserHolder,
    private val togglesHolder: TogglesHolder,
    private val localization: LocalizationService,
    private val securityManager: SecurityManager,
    private val trackingContext: TracingContext,
    private val logger: KVLogger,
    val request: HttpServletRequest,
) {
    companion object {
        const val ATTRIBUTE_IP = "com.wutsi.attributes.ip"
    }

    fun remoteIp(): String {
        val ip = request.getHeader("X-FORWARDED-FOR")
        return if (ip.isNullOrEmpty()) {
            request.remoteAddr
        } else {
            ip
        }
    }

    fun storeRemoteIp(ip: String?, request: HttpServletRequest) {
        if (!ip.isNullOrEmpty()) {
            request.session.setAttribute(ATTRIBUTE_IP, ip)
        } else {
            request.session.removeAttribute(ATTRIBUTE_IP)
        }
    }

    fun loadRemoteIp(request: HttpServletRequest): String? =
        request.session.getAttribute(ATTRIBUTE_IP)?.toString()

    fun isMobileUserAgent(): Boolean {
        val ua = UAgentInfo(
            request.getHeader("User-Agent"),
            request.getHeader("Accept"),
        )
        return ua.detectMobileQuick()
    }

    fun deviceId(): String = trackingContext.deviceId()

    fun currentSuperUser(): UserModel? =
        userHolder.superUser()

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

    fun siteId(): Long = 1L
}
