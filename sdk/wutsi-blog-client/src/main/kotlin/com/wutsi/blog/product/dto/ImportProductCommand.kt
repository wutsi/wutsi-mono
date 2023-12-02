package com.wutsi.blog.product.dto

data class ImportProductCommand(
    val storeId: String = "",
    val url: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
