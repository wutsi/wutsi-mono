package com.wutsi.blog.user.endpoints

import com.wutsi.blog.user.dto.UpdateUserAttributeCommand
import com.wutsi.blog.user.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping
class UpdateUserAttributeCommandExecutor(
    private val service: UserService,
) {
    @PostMapping("/v1/users/commands/update-attribute")
    fun execute(@Valid @RequestBody command: UpdateUserAttributeCommand) {
        service.updateAttribute(command)
    }
}
