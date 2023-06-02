package com.wutsi.blog.user.endpoints

import com.wutsi.blog.user.dto.CreateBlogCommand
import com.wutsi.blog.user.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping
class CreateBlogCommandExecutor(
    private val service: UserService,
) {
    @PostMapping("/v1/users/commands/create-blog")
    fun execute(@Valid @RequestBody command: CreateBlogCommand) {
        service.createBlog(command)
    }
}
