package com.wutsi.blog.product.dto

import jakarta.validation.constraints.NotEmpty

data class UpdateProductAttributeCommand(
    val productId: Long = -1,
    @get:NotEmpty val name: String = "",
    val value: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
