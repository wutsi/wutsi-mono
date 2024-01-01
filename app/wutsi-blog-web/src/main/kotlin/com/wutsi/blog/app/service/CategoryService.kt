package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.CategoryBackend
import com.wutsi.blog.app.mapper.CategoryMapper
import com.wutsi.blog.app.model.CategoryModel
import com.wutsi.blog.product.dto.SearchCategoryRequest
import org.springframework.stereotype.Component

@Component
class CategoryService(
    private val backend: CategoryBackend,
    private val mapper: CategoryMapper,
) {
    fun search(request: SearchCategoryRequest): List<CategoryModel> =
        backend.search(request).categories.map { category -> mapper.toCategoryModel(category) }
}
