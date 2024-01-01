package com.wutsi.blog.product.dto

data class SearchCategoryRequest(
    val parentId: Long? = null,
    val categoryIds: List<Long> = emptyList(),
    val keyword: String? = null,
    val language: String? = null,
    val level: Int? = null,
    val limit: Int = 20,
    val offset: Int = 0,
)
