package com.wutsi.blog.app.backend

import com.wutsi.blog.client.user.AuthenticateRequest
import com.wutsi.blog.client.user.AuthenticateResponse
import com.wutsi.blog.client.user.GetSessionResponse
import com.wutsi.blog.client.user.RunAsRequest
import com.wutsi.blog.client.user.RunAsResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AuthenticationBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.authentication.endpoint}")
    private lateinit var endpoint: String

    fun login(request: AuthenticateRequest): AuthenticateResponse {
        return rest.postForEntity(endpoint, request, AuthenticateResponse::class.java).body!!
    }

    fun logout(token: String) {
        val url = "$endpoint/$token"
        rest.delete(url)
    }

    fun session(token: String): GetSessionResponse {
        return rest.getForEntity("$endpoint/$token", GetSessionResponse::class.java).body!!
    }

    fun runAs(request: RunAsRequest): RunAsResponse {
        return rest.postForEntity("$endpoint/as", request, RunAsResponse::class.java).body!!
    }
}
