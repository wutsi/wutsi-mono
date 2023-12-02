package com.wutsi.blog.product.dto

data class ProductImportedEventPayload(
    val url: String = "",
    val errorUrl: String? = null,
)
