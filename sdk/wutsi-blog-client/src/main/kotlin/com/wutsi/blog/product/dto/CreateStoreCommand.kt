package com.wutsi.blog.product.dto

data class CreateStoreCommand(
    val userId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
