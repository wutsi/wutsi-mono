package com.wutsi.blog.product.dto

import javax.validation.constraints.NotBlank

data class CreateProductCommand(
    val userId: Long = -1,
    val handle: String = "",
    @get:NotBlank val title: String = "",
    @get:NotBlank val fileUrl: String = "",
    val description: String? = null,
    val thumbnailUrl: String? = null,
    val price: Long = 0,
    val discountPercentage: Int = 0,
    val currency: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
