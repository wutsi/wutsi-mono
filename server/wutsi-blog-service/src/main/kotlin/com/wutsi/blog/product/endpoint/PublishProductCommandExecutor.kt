package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.PublishProductCommand
import com.wutsi.blog.product.service.ProductService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class PublishProductCommandExecutor(
    private val service: ProductService,
) {
    @PostMapping("/v1/products/commands/publish")
    fun execute(@Valid @RequestBody command: PublishProductCommand) {
        service.publish(command)
    }
}
