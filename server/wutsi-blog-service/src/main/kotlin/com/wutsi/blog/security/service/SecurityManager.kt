package com.wutsi.blog.security.service

import com.wutsi.blog.account.domain.Session
import com.wutsi.blog.account.service.AuthenticationService
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ForbiddenException
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class SecurityManager(
    private val authService: AuthenticationService,
    private val request: HttpServletRequest,
    private val storyDao: StoryRepository,
) {
    fun checkUser(userId: Long) {
        val currentUserId = getCurrentUserId()
        if (userId != currentUserId) {
            throw ForbiddenException(
                Error(
                    code = "permission_denied",
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
                val session = authService.findByAccessToken(token)
                session.runAsUser?.id ?: session.account.user.id
            }
        } catch (ex: Exception) {
            return null
        }
    }

    fun getCurrentSession(): Session? {
        try {
            val token = getToken()
            return token?.let {
                authService.findByAccessToken(token)
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
