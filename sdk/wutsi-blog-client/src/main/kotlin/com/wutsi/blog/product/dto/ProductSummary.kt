package com.wutsi.blog.product.dto

data class ProductSummary(
    val id: Long = -1,
    val userId: Long = -1,
    val title: String = "",
    val imageUrl: String? = null,
    val price: Long = 0,
    val currency: String = "",
    val available: Boolean = true,
)
