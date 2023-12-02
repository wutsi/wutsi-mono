package com.wutsi.blog.product.dto

import javax.validation.constraints.NotBlank

data class CreateStoreCommand(
    val userId: Long = -1,
    @get:NotBlank val feedUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
