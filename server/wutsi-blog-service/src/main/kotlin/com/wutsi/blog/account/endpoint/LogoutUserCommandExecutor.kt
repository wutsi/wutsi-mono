package com.wutsi.blog.account.endpoint

import com.wutsi.blog.account.dto.LoginUserAsCommand
import com.wutsi.blog.account.dto.LoginUserResponse
import com.wutsi.blog.account.service.AccountService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/auth/commands/run-as")
class LoginUserAsCommandExecutor(
    private val service: AccountService,
) {
    @PostMapping
    fun get(@Valid @RequestBody command: LoginUserAsCommand): LoginUserResponse =
        LoginUserResponse(
            accessToken = service.runAs(command).accessToken
        )
}
