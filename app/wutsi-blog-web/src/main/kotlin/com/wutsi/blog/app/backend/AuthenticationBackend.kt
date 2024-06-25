package com.wutsi.blog.app.backend

import com.wutsi.blog.account.dto.CreateLoginLinkCommand
import com.wutsi.blog.account.dto.GetLoginLinkResponse
import com.wutsi.blog.account.dto.GetSessionResponse
import com.wutsi.blog.account.dto.LoginUserAsCommand
import com.wutsi.blog.account.dto.LoginUserCommand
import com.wutsi.blog.account.dto.LoginUserResponse
import com.wutsi.blog.account.dto.LogoutUserCommand
import com.wutsi.blog.event.EventType
import com.wutsi.platform.core.stream.EventStream
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.Cache
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AuthenticationBackend(
	private val rest: RestTemplate,
	private val eventStream: EventStream,
	private val cache: Cache,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(AuthenticationBackend::class.java)
    }

    @Value("\${wutsi.application.backend.authentication.endpoint}")
    private lateinit var endpoint: String

    fun login(request: LoginUserCommand): LoginUserResponse =
        rest.postForEntity("$endpoint/commands/login", request, LoginUserResponse::class.java).body!!

    fun loginAs(request: LoginUserAsCommand): LoginUserResponse =
        rest.postForEntity("$endpoint/commands/login-as", request, LoginUserResponse::class.java).body!!

    fun logout(token: String) {
        val request = LogoutUserCommand(token)
        rest.postForEntity("$endpoint/commands/logout", request, Any::class.java)
        cacheEvict(token)
    }

    fun session(token: String): GetSessionResponse {
        var response = cacheGet(token)
        if (response == null) {
            response = rest.getForEntity("$endpoint/sessions/$token", GetSessionResponse::class.java).body!!
            cachePut(token, response)
        }
        return response
    }

    fun createLink(request: CreateLoginLinkCommand) {
        eventStream.publish(EventType.CREATE_LOGIN_LINK_COMMAND, request)
    }

    fun getLink(id: String): GetLoginLinkResponse =
        rest.getForEntity("$endpoint/links/$id", GetLoginLinkResponse::class.java).body!!

    private fun cacheEvict(key: String) {
        try {
            cache.evict(key)
        } catch (ex: Exception) {
            LOGGER.warn("Caching error", ex)
        }
    }

    private fun cacheGet(key: String): GetSessionResponse? =
        try {
            cache.get(key, GetSessionResponse::class.java)
        } catch (ex: Exception) {
            LOGGER.warn("Caching error", ex)
            null
        }

    private fun cachePut(key: String, value: GetSessionResponse) {
        try {
            cache.put(key, value)
        } catch (ex: Exception) {
            LOGGER.warn("Caching error", ex)
        }
    }
}
