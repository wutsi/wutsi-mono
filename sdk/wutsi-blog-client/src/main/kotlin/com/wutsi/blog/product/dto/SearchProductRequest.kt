package com.wutsi.blog.product.dto

import com.wutsi.blog.SortOrder

data class SearchProductRequest(
    val storeIds: List<String> = emptyList(),
    val productIds: List<Long> = emptyList(),
    val externalIds: List<String> = emptyList(),
    val excludeProductIds: List<Long> = emptyList(),
    val available: Boolean? = null,
    val sortBy: ProductSortStrategy = ProductSortStrategy.PUBLISHED,
    val sortOrder: SortOrder = SortOrder.DESCENDING,
    val status: ProductStatus? = null,
    val limit: Int = 20,
    val offset: Int = 0,
)
