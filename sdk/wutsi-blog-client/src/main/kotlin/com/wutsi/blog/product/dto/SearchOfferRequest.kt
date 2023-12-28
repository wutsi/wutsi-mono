package com.wutsi.blog.product.dto

data class SearchOfferRequest(
    val userId: Long? = null,
    val productIds: List<Long> = emptyList()
)
