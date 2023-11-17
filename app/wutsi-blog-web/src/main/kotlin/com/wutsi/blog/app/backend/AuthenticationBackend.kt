package com.wutsi.blog.app.backend

import com.wutsi.blog.account.dto.CreateLoginLinkCommand
import com.wutsi.blog.account.dto.CreateLoginLinkResponse
import com.wutsi.blog.account.dto.GetSessionResponse
import com.wutsi.blog.account.dto.LoginUserAsCommand
import com.wutsi.blog.account.dto.LoginUserCommand
import com.wutsi.blog.account.dto.LoginUserResponse
import com.wutsi.blog.account.dto.LogoutUserCommand
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AuthenticationBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.authentication.endpoint}")
    private lateinit var endpoint: String

    fun login(request: LoginUserCommand): LoginUserResponse =
        rest.postForEntity("$endpoint/commands/login", request, LoginUserResponse::class.java).body!!

    fun loginAs(request: LoginUserAsCommand): LoginUserResponse =
        rest.postForEntity("$endpoint/commands/login-as", request, LoginUserResponse::class.java).body!!

    fun logout(token: String) {
        val request = LogoutUserCommand(token)
        rest.postForEntity("$endpoint/commands/logout", request, Any::class.java)
    }

    fun session(token: String): GetSessionResponse {
        return rest.getForEntity("$endpoint/sessions/$token", GetSessionResponse::class.java).body!!
    }

    fun createLink(request: CreateLoginLinkCommand): CreateLoginLinkResponse =
        rest.postForEntity("$endpoint/commands/links/create", request, CreateLoginLinkResponse::class.java)
}
