package com.wutsi.blog.product.dto

data class SearchDiscountRequest(
    val userId: Long = -1,
    val storeId: String = ""
)
