package com.wutsi.blog.product.service

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.service.filter.PurchasedProductSearchFilter
import com.wutsi.blog.product.service.filter.TaggedProductSearchFilter
import org.springframework.stereotype.Service

@Service
class ProductSearchFilterSet(
    private val purchased: PurchasedProductSearchFilter,
    private val tagged: TaggedProductSearchFilter,
) : ProductSearchFilter {
    private val filters = listOf(
        tagged,
        purchased, // IMPORTANT: Must be the last
    )

    override fun filter(request: SearchProductRequest, products: List<ProductEntity>): List<ProductEntity> {
        var result = products
        filters.forEach { item ->
            result = item.filter(request, result)
        }
        return result
    }
}
