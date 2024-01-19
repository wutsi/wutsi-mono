package com.wutsi.blog.product.dto

data class PublishProductCommand(
    val productId: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
)
