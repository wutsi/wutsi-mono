package com.wutsi.blog.product.dto

data class CreateProductCommand(
    val type: ProductType = ProductType.UNKNOWN,
    val storeId: String = "",
    val externalId: String = "",
    val categoryId: Long = -1,
    val title: String = "",
    val description: String? = null,
    val price: Long = 0,
    val available: Boolean = true,
)
