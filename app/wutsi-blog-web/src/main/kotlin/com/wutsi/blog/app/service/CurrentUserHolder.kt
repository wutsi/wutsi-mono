package com.wutsi.blog.app.service

import com.wutsi.blog.app.model.UserModel
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
    private val tokenStorage: AccessTokenStorage,
    private val sessionHolder: CurrentSessionHolder,
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
        if (user == null) {
            val session = sessionHolder.session()
                ?: return null

            val userId = session.runAsUserId ?: session.userId
            try {
                user = userService.get(userId)
            } catch (e: Exception) {
                LOGGER.warn("Unable to resolve User $userId", e)
                tokenStorage.delete(response)
            }
        }
        return user
    }
}
