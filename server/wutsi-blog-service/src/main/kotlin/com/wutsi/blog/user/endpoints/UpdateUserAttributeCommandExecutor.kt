package com.wutsi.blog.user.endpoints

import com.wutsi.blog.user.it.UpdateUserAttributeCommand
import com.wutsi.blog.user.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/v1/users/commands/update-attribute")
@RequestMapping
class UpdateUserAttributeCommandController(
    private val service: UserService,
) {
    @PostMapping()
    fun execute(command: UpdateUserAttributeCommand) {
        service.updateAttribute(command)
    }
}
