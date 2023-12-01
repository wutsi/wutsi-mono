package com.wutsi.blog.product.dto

import java.util.Date

data class Product(
    val id: Long = -1,
    val storyId: String = "",
    val externalId: String = "",
    val title: String = "",
    val description: String? = null,
    val imageUrl: String? = null,
    val fileUrl: String? = null,
    val price: Long = 0,
    val currency: String = "",
    val available: Boolean = true,
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
    val orderCount: Long = 0,
    val totalSales: Long = 0,
)
