package com.wutsi.blog.product.dto

data class SearchProductRequest(
    val userId: Long? = null,
    val productIds: List<Long> = emptyList(),
    val limit: Int = 20,
    val offset: Int = 0
)
