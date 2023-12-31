package com.wutsi.blog.product.dto

import javax.validation.constraints.NotEmpty

data class UpdateProductAttributeCommand(
    val productId: Long = -1,
    @get:NotEmpty val name: String = "",
    val value: String? = null,
)
