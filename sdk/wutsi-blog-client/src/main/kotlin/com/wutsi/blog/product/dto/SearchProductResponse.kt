package com.wutsi.blog.product.dto

data class SearchProductResponse(
    val products: List<ProductSummary> = emptyList()
)
