package com.wutsi.blog.product.dto

import java.util.Date

data class Store(
    val id: String = "",
    val userId: Long = -1,
    val currency: String = "",
    val feedUrl: String = "",
    val productCount: Long = 0,
    val orderCount: Long = 0,
    val totalSales: Long = 0,
    val creationDateTime: Date = Date(),
    var modificationDateTime: Date = Date(),
)
