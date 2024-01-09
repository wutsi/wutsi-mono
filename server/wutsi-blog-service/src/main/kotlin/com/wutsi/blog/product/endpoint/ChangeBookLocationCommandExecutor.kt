package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.ChangeBookLocationCommand
import com.wutsi.blog.product.service.BookService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class ChangeBookLocationCommandExecutor(private val service: BookService) {
    @PostMapping("/v1/books/commands/change-location")
    fun execute(@Valid @RequestBody command: ChangeBookLocationCommand) {
        service.changeLocation(command)
    }
}
