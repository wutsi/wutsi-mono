package com.wutsi.blog.product.service.filter

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.ProductSortStrategy
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.service.ProductSearchFilter
import com.wutsi.blog.story.dao.PreferredCategoryRepository
import com.wutsi.blog.story.domain.PreferredCategoryEntity
import org.springframework.stereotype.Service

@Service
class PreferredCategoryProductSearchFilter(
    private val preferredCategoryDao: PreferredCategoryRepository,
) : ProductSearchFilter {
    companion object {
        const val MAX_CATEGORIES = 5
    }

    override fun filter(request: SearchProductRequest, products: List<ProductEntity>): List<ProductEntity> {
        val userId = request.searchContext?.userId
        if (request.sortBy != ProductSortStrategy.RECOMMENDED || userId == null || products.isEmpty()) {
            return products
        }

        val categories: List<PreferredCategoryEntity?> = preferredCategoryDao.findByUserIdOrderByTotalReadsDesc(userId)
        val categoryIds = categories
            .map { category -> category?.categoryId }
            .filterNotNull()
            .take(MAX_CATEGORIES)
        if (categoryIds.isEmpty()) {
            return products
        }

        val result = mutableListOf<ProductEntity>()
        categoryIds.forEach { categoryId ->
            result.addAll(
                products.filter { product -> product.category?.id == categoryId }
            )
        }
        result.addAll(
            products.filter { product ->
                product.category == null || !categoryIds.contains(product.category?.id)
            }
        )
        return result
    }
}
