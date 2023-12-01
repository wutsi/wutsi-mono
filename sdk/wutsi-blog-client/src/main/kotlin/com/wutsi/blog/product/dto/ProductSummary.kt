package com.wutsi.blog.product.dto

import java.util.Date

data class ProductSummary(
    val id: Long = -1,
    val userId: Long = -1,
    val externalId: String = "",
    val title: String = "",
    val imageUrl: String? = null,
    val price: Long = 0,
    val currency: String = "",
    val available: Boolean = true,
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
)
