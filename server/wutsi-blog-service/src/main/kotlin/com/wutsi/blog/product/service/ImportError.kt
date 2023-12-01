package com.wutsi.blog.product.service

data class ImportError(
    val row: Int,
    val errorCode: String,
)
