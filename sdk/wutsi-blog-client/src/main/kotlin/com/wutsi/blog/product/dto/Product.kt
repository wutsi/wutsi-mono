package com.wutsi.blog.product.dto

import java.util.Date

data class Product(
    val id: Long = -1,
    val storeId: String = "",
    val externalId: String? = null,
    val title: String = "",
    val description: String? = null,
    val imageUrl: String? = null,
    val fileUrl: String? = null,
    val fileContentLength: Long = 0,
    val fileContentType: String? = null,
    val price: Long = 0,
    val currency: String = "",
    val available: Boolean = true,
    val slug: String = "",
    val status: ProductStatus = ProductStatus.DRAFT,
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
    val orderCount: Long = 0,
    val totalSales: Long = 0,
    val viewCount: Long = 0,
    val category: Category? = null,
    val language: String? = null,
    val numberOfPages: Int? = null,
    val type: ProductType = ProductType.UNKNOWN,
    val liretamaUrl: String? = null,
)
