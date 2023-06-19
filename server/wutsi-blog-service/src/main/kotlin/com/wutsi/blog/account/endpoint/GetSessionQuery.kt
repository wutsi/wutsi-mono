package com.wutsi.blog.account.endpoint

import com.wutsi.blog.account.dto.GetSessionResponse
import com.wutsi.blog.account.dto.Session
import com.wutsi.blog.account.service.LoginService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetSessionQuery(
    private val service: LoginService,
) {
    @GetMapping("/v1/auth/sessions/{accessToken}")
    fun get(@PathVariable accessToken: String): GetSessionResponse {
        val session = service.findSession(accessToken)
        return GetSessionResponse(
            session = Session(
                accountId = session.account.id!!,
                userId = session.account.user.id!!,
                runAsUserId = session.runAsUser?.id,
                accessToken = session.accessToken,
                refreshToken = session.refreshToken,
                loginDateTime = session.loginDateTime,
                logoutDateTime = session.logoutDateTime,
            )
        )
    }
}
