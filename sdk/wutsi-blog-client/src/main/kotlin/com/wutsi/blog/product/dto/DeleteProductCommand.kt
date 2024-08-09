package com.wutsi.blog.product.dto

data class DeleteProductCommand(
    val productId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
