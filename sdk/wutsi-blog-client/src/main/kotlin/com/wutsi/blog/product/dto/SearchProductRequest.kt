package com.wutsi.blog.product.dto

import com.wutsi.blog.SortOrder
import java.util.Date

data class SearchProductRequest(
    val storeIds: List<String> = emptyList(),
    val productIds: List<Long> = emptyList(),
    val externalIds: List<String> = emptyList(),
    val excludeProductIds: List<Long> = emptyList(),
    val type: ProductType? = null,
    val storyId: Long? = null,
    val available: Boolean? = null,
    val publishedStartDate: Date? = null,
    val publishedEndDate: Date? = null,
    val sortBy: ProductSortStrategy = ProductSortStrategy.PUBLISHED,
    val sortOrder: SortOrder = SortOrder.DESCENDING,
    val status: ProductStatus? = null,
    val limit: Int = 20,
    val offset: Int = 0,
)
