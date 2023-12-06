package com.wutsi.blog.product.dto

import javax.validation.constraints.NotBlank

data class ImportProductCommand(
    @get:NotBlank val storeId: String = "",
    @get:NotBlank val url: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
