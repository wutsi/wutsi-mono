package com.wutsi.blog.product.dto

import java.util.Date

data class BookSummary(
    val id: Long = 0,
    val userId: Long = -1,
    val productId: Long = -1,
    val readPercentage: Int = 0,
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
)
