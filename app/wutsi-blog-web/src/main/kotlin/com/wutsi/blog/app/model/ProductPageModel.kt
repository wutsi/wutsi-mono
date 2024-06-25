package com.wutsi.blog.app.model

data class ProductPageModel(
    val id: Long = -1,
    val productId: Long = -1,
    val contentUrl: String = "",
    val contentType: String = "",
    val number: Int = 0,
)