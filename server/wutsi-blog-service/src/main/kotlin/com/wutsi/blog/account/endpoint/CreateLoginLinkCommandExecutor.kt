package com.wutsi.blog.account.endpoint

import com.wutsi.blog.account.dto.CreateLoginLinkCommand
import com.wutsi.blog.account.dto.CreateLoginLinkResponse
import com.wutsi.blog.account.service.LoginService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/auth/commands/create-link")
class CreateLoginLinkCommandExecutor(
    private val service: LoginService,
) {
    @PostMapping
    fun get(@Valid @RequestBody command: CreateLoginLinkCommand): CreateLoginLinkResponse {
        val id = service.createLoginLink(command)
        return CreateLoginLinkResponse(id)
    }
}
