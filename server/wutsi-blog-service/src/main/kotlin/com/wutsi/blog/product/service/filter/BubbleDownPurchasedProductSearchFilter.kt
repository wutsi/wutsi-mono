package com.wutsi.blog.product.service.filter

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.service.ProductSearchFilter
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.platform.payment.core.Status
import org.springframework.stereotype.Service

@Service
class BubbleDownPurchasedProductSearchFilter(
    private val transactionDao: TransactionRepository,
) : ProductSearchFilter {
    override fun filter(request: SearchProductRequest, products: List<ProductEntity>): List<ProductEntity> {
        val userId = request.searchContext?.userId
        if (!request.bubbleDownPurchasedProduct || userId == null || products.isEmpty()) {
            return products
        }

        val productIds = transactionDao
            .findByUserIdByTypeByStatus(userId, TransactionType.CHARGE, Status.SUCCESSFUL)
            .mapNotNull { tx -> tx.product?.id }
            .toSet()
        if (productIds.isEmpty()) {
            return products
        }

        val result = mutableListOf<ProductEntity>()
        result.addAll(
            products.filterNot { product -> productIds.contains(product.id) }
        )
        result.addAll(
            products.filter { product -> productIds.contains(product.id) }
        )
        return result
    }
}
