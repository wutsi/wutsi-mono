package com.wutsi.blog.security.service

import com.wutsi.blog.account.domain.SessionEntity
import com.wutsi.blog.account.service.LoginService
import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ForbiddenException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class SecurityManager(
    private val authService: LoginService,
    private val request: HttpServletRequest,
    private val storyDao: StoryRepository,
    private val userDao: UserRepository,
) {
    fun checkSuperUser() {
        val user = getCurrentUserId()?.let { userId ->
            userDao.findById(userId).getOrNull()
        }
        if (user?.superUser != true) {
            throw ForbiddenException(
                Error(
                    code = ErrorCode.PERMISSION_DENIED,
                ),
            )
        }
    }

    fun checkUser(userId: Long) {
        val currentUserId = getCurrentUserId()
        if (userId != currentUserId) {
            throw ForbiddenException(
                Error(
                    code = ErrorCode.PERMISSION_DENIED,
                    data = mapOf(
                        "user_id" to userId,
                        "current_user_id" to currentUserId.toString(),
                    ),
                ),
            )
        }
    }

    fun checkStoryOwnership(storyId: Long) {
        val story = storyDao.findById(storyId)
        val currentUserId = getCurrentUserId()
        if (story.isPresent && story.get().userId != currentUserId) {
            throw ForbiddenException(
                Error(
                    code = "ownership_error",
                    data = mapOf(
                        "current_user_id" to currentUserId.toString(),
                    ),
                ),
            )
        }
    }

    fun getCurrentUserId(): Long? {
        try {
            val token = getToken()
            return token?.let {
                val session = authService.findSession(token)
                session.runAsUser?.id ?: session.account.user.id
            }
        } catch (ex: Exception) {
            return null
        }
    }

    fun getCurrentSession(): SessionEntity? {
        try {
            val token = getToken()
            return token?.let {
                authService.findSession(token)
            }
        } catch (ex: Exception) {
            return null
        }
    }

    fun getToken(): String? {
        val value = request.getHeader("Authorization") ?: return null
        return if (value.startsWith("Bearer ", ignoreCase = true)) {
            value.substring(7)
        } else {
            null
        }
    }
}
