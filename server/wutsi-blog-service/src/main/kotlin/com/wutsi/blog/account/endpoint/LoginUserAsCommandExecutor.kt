package com.wutsi.blog.account

import com.wutsi.blog.account.mapper.SessionMapper
import com.wutsi.blog.account.service.AuthenticationService
import com.wutsi.blog.client.event.LoginEvent
import com.wutsi.blog.client.user.AuthenticateRequest
import com.wutsi.blog.client.user.AuthenticateResponse
import com.wutsi.blog.client.user.GetSessionResponse
import com.wutsi.blog.client.user.RunAsRequest
import com.wutsi.blog.client.user.RunAsResponse
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/auth")
class AuthenticationController(
    private val auth: AuthenticationService,
    private val mapper: SessionMapper,
    private val events: ApplicationEventPublisher,
) {
    @GetMapping("/{access_token}")
    fun get(@PathVariable(name = "access_token") token: String): GetSessionResponse {
        val session = auth.findByAccessToken(token)
        return GetSessionResponse(
            session = mapper.toSessionDto(session),
        )
    }

    @PostMapping()
    fun login(
        @RequestBody @Valid request: AuthenticateRequest,
        @RequestHeader(required = false, name = TracingContext.HEADER_DEVICE_ID) deviceUID: String?,
        @RequestHeader(required = false, name = HttpHeaders.USER_AGENT) userAgent: String?,
    ): AuthenticateResponse {
        val response = auth.login(request)
        events.publishEvent(
            LoginEvent(
                userId = response.userId,
                loginCount = response.loginCount,
                sessionId = response.sessionId,
                deviceUID = deviceUID,
                userAgent = userAgent,
            ),
        )
        return response
    }

    @DeleteMapping("/{access_token}")
    fun logout(@PathVariable(name = "access_token") token: String) =
        auth.logout(token)

    @PostMapping("/as")
    fun runAs(@RequestBody @Valid request: RunAsRequest): RunAsResponse =
        auth.runAs(request)
}
