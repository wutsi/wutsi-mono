package com.wutsi.blog.product.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty

data class CreateProductCommand(
    val type: ProductType = ProductType.UNKNOWN,
    @get:NotEmpty val storeId: String = "",
    val externalId: String? = null,
    val categoryId: Long = -1,
    @get:NotEmpty val title: String = "",
    val description: String? = null,
    @get:Min(0) val price: Long = 0,
    val available: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
)
