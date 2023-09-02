package com.wutsi.blog.app.security.oauth

import com.wutsi.blog.app.service.AccessTokenStorage
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.RememberMeServices
import org.springframework.stereotype.Component

@Deprecated("")
@Component
class OAuthRememberMeService(
    private val storage: AccessTokenStorage,
) : RememberMeServices {
    override fun loginSuccess(request: HttpServletRequest, response: HttpServletResponse, auth: Authentication) {
        if (auth is OAuthTokenAuthentication) {
            storage.put(auth.accessToken, request, response)
        }
    }

    override fun autoLogin(request: HttpServletRequest, response: HttpServletResponse): Authentication? {
        return null
    }

    override fun loginFail(request: HttpServletRequest, response: HttpServletResponse) {
    }
}
