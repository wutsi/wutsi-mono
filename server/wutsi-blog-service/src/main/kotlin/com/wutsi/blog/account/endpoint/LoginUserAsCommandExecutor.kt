package com.wutsi.blog.account.endpoint

import com.wutsi.blog.account.dto.LoginUserAsCommand
import com.wutsi.blog.account.dto.LoginUserResponse
import com.wutsi.blog.account.service.LoginService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/auth/commands/login-as")
class LoginUserAsCommandExecutor(
    private val service: LoginService,
) {
    @PostMapping
    fun get(@Valid @RequestBody command: LoginUserAsCommand): LoginUserResponse =
        LoginUserResponse(
            accessToken = service.loginAs(command).accessToken,
        )
}
