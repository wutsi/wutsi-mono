package com.wutsi.blog.product.service.filter

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.service.ProductSearchFilter
import org.springframework.stereotype.Service

@Service
class DedupUserProductSearchFilter : ProductSearchFilter {
    override fun filter(request: SearchProductRequest, products: List<ProductEntity>): List<ProductEntity> {
        if (!request.dedupUser) {
            return products
        }

        val userIds = mutableSetOf<String?>()
        return products.filter { userIds.add(it.store.id) }
    }
}
