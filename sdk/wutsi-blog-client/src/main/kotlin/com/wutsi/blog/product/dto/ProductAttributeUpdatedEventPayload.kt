package com.wutsi.blog.product.dto

data class ProductAttributeUpdatedEventPayload(
    val name: String = "",
    val value: String? = null,
)
