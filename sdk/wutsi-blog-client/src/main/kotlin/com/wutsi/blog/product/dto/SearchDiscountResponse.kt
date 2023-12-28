package com.wutsi.blog.product.dto

data class SearchDiscountResponse(
    val discounts: List<Discount> = emptyList()
)
