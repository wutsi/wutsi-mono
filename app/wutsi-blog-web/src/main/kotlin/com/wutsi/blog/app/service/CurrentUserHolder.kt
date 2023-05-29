package com.wutsi.blog.app.service

import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.login.service.AccessTokenStorage
import com.wutsi.platform.core.error.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class CurrentUserHolder(
    private val userService: UserService,
    private val sessionHolder: CurrentSessionHolder,
    private val tokenStorage: AccessTokenStorage,
    val request: HttpServletRequest,
    val response: HttpServletResponse,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CurrentUserHolder::class.java)
    }

    private var user: UserModel? = null
    private var superUser: UserModel? = null

    fun superUser(): UserModel? {
        if (superUser != null) {
            return superUser
        }

        val user = user()
        if (user != null && user.superUser) {
            superUser = user
        } else {
            val session = sessionHolder.session()
                ?: return null

            if (session.runAsUserId != null) {
                superUser = userService.get(session.runAsUserId)
            }
        }

        return superUser
    }

    fun user(): UserModel? {
        if (user != null) {
            return user
        }

        val session = sessionHolder.session()
            ?: return null

        try {
            if (session.runAsUserId != null) {
                user = userService.get(session.runAsUserId)
            } else {
                user = userService.get(session.userId)
            }
        } catch (e: Exception) {
            LOGGER.warn("Unable to resolve user ${session.userId}", e)
            if (e is NotFoundException) {
                tokenStorage.delete(response)
            }
        }

        return user
    }
}
