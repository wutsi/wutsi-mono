package com.wutsi.blog.product.dto

import java.util.Date

data class Book(
    val id: Long = 0,
    val userId: Long = -1,
    val transactionId: String = "",
    var location: String? = null,
    val readPercentage: Int = 0,
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
    val expiryDate: Date? = null,
    val product: Product = Product(),
)
