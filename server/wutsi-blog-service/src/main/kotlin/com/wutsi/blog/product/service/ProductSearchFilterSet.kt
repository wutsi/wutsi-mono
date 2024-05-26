package com.wutsi.blog.product.service

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.service.filter.BubbleDownPurchasedProductSearchFilter
import com.wutsi.blog.product.service.filter.PreferredCategoryProductSearchFilter
import com.wutsi.blog.product.service.filter.TaggedProductSearchFilter
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
class ProductSearchFilterSet(
    private val preferredCategoryProductSearchFilter: PreferredCategoryProductSearchFilter,
    private val bubbleDownPurchasedProductSearchFilter: BubbleDownPurchasedProductSearchFilter,
    private val taggedProductSearchFilter: TaggedProductSearchFilter,
    private val logger: KVLogger,
) : ProductSearchFilter {
    private val filters = listOf(
        preferredCategoryProductSearchFilter,
        taggedProductSearchFilter, // IMPORTANT: Must be before the last
        bubbleDownPurchasedProductSearchFilter, // IMPORTANT: Must be the last
    )

    override fun filter(request: SearchProductRequest, products: List<ProductEntity>): List<ProductEntity> {
        if (products.isEmpty()) {
            return products
        }

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
