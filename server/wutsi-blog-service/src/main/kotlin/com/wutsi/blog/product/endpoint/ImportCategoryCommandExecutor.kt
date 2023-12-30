package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.service.CategoryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class ImportCategoryCommandExecutor(private val service: CategoryService) {
    @GetMapping("/v1/categories/commands/import")
    fun execute() {
        service.import()
    }
}
