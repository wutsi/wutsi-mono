package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.UpdateProductAttributeCommand
import com.wutsi.blog.product.service.ProductService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class UpdateProductAttributeCommandExecutor(
    private val service: ProductService
) {
    @PostMapping("/v1/products/commands/update-attribute")
    fun execute(@Valid @RequestBody command: UpdateProductAttributeCommand) {
        service.updateAttribute(command)
    }
}
