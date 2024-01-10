package com.wutsi.blog.product.dto

data class SearchBookRequest(
    val userId: Long? = null,
    val bookIds: List<Long> = emptyList(),
    val productIds: List<Long> = emptyList(),
    val transactionId: String? = null,
    val limit: Int = 20,
    val offset: Int = 0,
)
