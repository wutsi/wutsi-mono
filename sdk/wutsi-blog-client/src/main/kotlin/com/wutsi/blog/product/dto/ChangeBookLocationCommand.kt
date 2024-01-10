package com.wutsi.blog.product.dto

data class ChangeBookLocationCommand(
    val bookId: Long = 1,
    val location: String = "",
)
