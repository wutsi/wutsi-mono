package com.wutsi.blog.product.dto

data class SearchCategoryResponse(
    val categories: List<Category> = emptyList()
)
