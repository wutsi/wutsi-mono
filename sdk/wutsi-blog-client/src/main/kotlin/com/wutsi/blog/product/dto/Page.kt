package com.wutsi.blog.product.dto

data class Page(
    val id: Long = 0,
    val productId: Long = -1,
    val contentType: String = "",
    val contentUrl: String = "",
    val number: Int = 0,
)
