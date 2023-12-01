package com.wutsi.blog.product.dto

data class ImportProductCommand(
    val userId: Long = -1,
    val url: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
