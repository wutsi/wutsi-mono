package com.wutsi.blog.product.service.filter

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.service.ProductSearchFilter
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.platform.payment.core.Status
import org.springframework.stereotype.Service

@Service
class PurchasedProductSearchFilter(
    private val transactionDao: TransactionRepository,
) : ProductSearchFilter {
    override fun filter(request: SearchProductRequest, products: List<ProductEntity>): List<ProductEntity> {
        if (request.currentUserId == null || products.isEmpty()) {
            return products
        }

        val productIds = transactionDao.findByUserIdByTypeByStatus(
            request.currentUserId!!,
            TransactionType.CHARGE,
            Status.SUCCESSFUL
        ).mapNotNull { tx -> tx.product?.id }
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
