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
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AuthenticationBackend(
	private val rest: RestTemplate,
	private val eventStream: EventStream
) {
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

	fun createLink(request: CreateLoginLinkCommand) {
		eventStream.publish(EventType.CREATE_LOGIN_LINK_COMMAND, request)
	}

	fun getLink(id: String): GetLoginLinkResponse =
		rest.getForEntity("$endpoint/links/$id", GetLoginLinkResponse::class.java).body!!
}
