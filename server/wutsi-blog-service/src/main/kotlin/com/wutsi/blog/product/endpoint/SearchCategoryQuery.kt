package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.SearchCategoryRequest
import com.wutsi.blog.product.dto.SearchCategoryResponse
import com.wutsi.blog.product.mapper.CategoryMapper
import com.wutsi.blog.product.service.CategoryService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchCategoryQuery(private val service: CategoryService, private val mapper: CategoryMapper) {
    @PostMapping("/v1/categories/queries/search")
    fun execute(@Valid @RequestBody request: SearchCategoryRequest): SearchCategoryResponse =
        SearchCategoryResponse(
            categories = service.search(request).map { category -> mapper.toCategory(category) }
        )
}
