package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.CreateProductCommand
import com.wutsi.blog.product.dto.CreateProductResponse
import com.wutsi.blog.product.service.ProductService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class CreateProductCommandExecutor(
    private val service: ProductService
) {
    @PostMapping("/v1/products/commands/create")
    fun execute(@Valid @RequestBody command: CreateProductCommand) = CreateProductResponse(
        productId = service.create(command).id!!
    )
}
