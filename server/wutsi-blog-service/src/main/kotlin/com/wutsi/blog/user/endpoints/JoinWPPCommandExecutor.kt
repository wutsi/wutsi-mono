package com.wutsi.blog.user.endpoints

import com.wutsi.blog.security.service.SecurityManager
import com.wutsi.blog.user.dto.JoinWPPCommand
import com.wutsi.blog.user.service.UserService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class JoinWPPCommandExecutor(
    private val service: UserService,
    private val securityManager: SecurityManager,
) {
    @PostMapping("/v1/users/commands/join-wpp")
    fun execute(@Valid @RequestBody command: JoinWPPCommand) {
        securityManager.checkUser(command.userId)
        service.joinWPP(command)
    }
}
