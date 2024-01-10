package com.wutsi.blog.product.dto

import java.util.Date

data class Book(
    val id: Long = 0,
    val userId: Long = -1,
    val transactionId: String = "",
    val productId: Long = -1,
    var location: String? = null,
    val readPercentage: Int = 0,
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
)
