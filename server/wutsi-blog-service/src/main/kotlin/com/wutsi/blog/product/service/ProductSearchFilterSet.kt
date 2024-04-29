package com.wutsi.blog.product.service

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.service.filter.PurchasedProductSearchFilter
import com.wutsi.blog.product.service.filter.TaggedProductSearchFilter
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
class ProductSearchFilterSet(
    private val purchased: PurchasedProductSearchFilter,
    private val tagged: TaggedProductSearchFilter,
    private val logger: KVLogger,
) : ProductSearchFilter {
    private val filters = listOf(
        tagged,
        purchased, // IMPORTANT: Must be the last
    )

    override fun filter(request: SearchProductRequest, products: List<ProductEntity>): List<ProductEntity> {
        var count = 0
        logger.add("product_filter_$count", products.size)

        var result = products
        filters.forEach { filter ->
            result = filter.filter(request, result)

            count++
            logger.add("product_filter_${count}_" + filter.javaClass.simpleName, result.size)
        }
        return result
    }
}
