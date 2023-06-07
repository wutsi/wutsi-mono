package com.wutsi.blog.account.service

import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class SecurityManager(
    private val authService: AuthenticationService,
    private val request: HttpServletRequest,
) {
    fun getCurrentUserId(): Long? {
        try {
            val token = getToken()
            return token?.let {
                authService.findByAccessToken(token).account.user.id
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
