package com.wutsi.blog.account.endpoint

import com.wutsi.blog.account.dto.LogoutUserCommand
import com.wutsi.blog.account.service.LoginService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/auth/commands/logout")
class LogoutUserCommandExecutor(
    private val service: LoginService,
) {
    @PostMapping
    fun get(@Valid @RequestBody command: LogoutUserCommand) {
        service.logout(command)
    }
}
