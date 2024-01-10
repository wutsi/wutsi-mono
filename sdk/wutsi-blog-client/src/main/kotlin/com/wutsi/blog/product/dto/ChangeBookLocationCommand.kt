package com.wutsi.blog.product.dto

data class ChangeBookLocationCommand(
    val bookId: Long = 1,
    val location: String = "",
    val readPercentage: Int = 0,
    val readed: Boolean = false,
)
