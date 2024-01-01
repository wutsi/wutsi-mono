package com.wutsi.blog.product.dto

data class ProductSummary(
    val id: Long = -1,
    val storeId: String = "",
    val externalId: String? = null,
    val title: String = "",
    val imageUrl: String? = null,
    val fileUrl: String? = null,
    val fileContentLength: Long = 0,
    val fileContentType: String? = null,
    val price: Long = 0,
    val currency: String = "",
    val available: Boolean = true,
    val slug: String = "",
    val status: ProductStatus = ProductStatus.DRAFT,
    val orderCount: Long = 0,
    val totalSales: Long = 0,
    val viewCount: Long = 0,
    val categoryId: Long? = null,
    val type: ProductType = ProductType.UNKNOWN,
)
