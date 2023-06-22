package com.wutsi.blog.app.service

import com.wutsi.blog.account.dto.LoginUserAsCommand
import com.wutsi.blog.app.backend.AuthenticationBackend
import org.springframework.stereotype.Component

@Component
class AuthenticationService(
    private val backend: AuthenticationBackend,
    private val requestContext: RequestContext,
) {
    fun runAs(userName: String) {
        backend.loginAs(
            LoginUserAsCommand(
                userName = userName,
                accessToken = requestContext.accessToken()!!,
            ),
        )
    }

    fun loginUrl(url: String, redirectUrl: String?): String {
        return if (redirectUrl == null) url else "$url?redirect=$redirectUrl"
    }
}
