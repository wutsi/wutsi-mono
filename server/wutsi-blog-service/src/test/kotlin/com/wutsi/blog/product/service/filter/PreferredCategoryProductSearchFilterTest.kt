package com.wutsi.blog.product.service.filter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.product.domain.CategoryEntity
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.ProductSortStrategy
import com.wutsi.blog.product.dto.SearchProductContext
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.story.dao.PreferredCategoryRepository
import com.wutsi.blog.story.domain.PreferredCategoryEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class PreferredCategoryProductSearchFilterTest {
    private val preferredCategoryDao = mock<PreferredCategoryRepository>()
    private val filter = PreferredCategoryProductSearchFilter(preferredCategoryDao)

    private val products = listOf(
        ProductEntity(id = 10, category = null),
        ProductEntity(id = 11, category = CategoryEntity(id = 100)),
        ProductEntity(id = 12, category = CategoryEntity(id = 100)),
        ProductEntity(id = 13, category = CategoryEntity(id = 200))
    )

    @Test
    fun `not recommended search`() {
        val request = SearchProductRequest(
            sortBy = ProductSortStrategy.ORDER_COUNT,
            searchContext = SearchProductContext(
                userId = 1L,
            ),
        )

        val response = filter.filter(request, products)
        assertEquals(products.map { it.id }, response.map { it.id })
    }

    @Test
    fun `no current user`() {
        val request = SearchProductRequest(
            sortBy = ProductSortStrategy.RECOMMENDED,
            searchContext = SearchProductContext(
                userId = null,
            ),
        )

        val response = filter.filter(request, products)
        assertEquals(products.map { it.id }, response.map { it.id })
    }

    @Test
    fun filter() {
        doReturn(
            listOf(
                PreferredCategoryEntity(categoryId = 100),
                PreferredCategoryEntity(categoryId = 500),
            )
        ).whenever(preferredCategoryDao).findByUserIdOrderByTotalReadsDesc(any())

        val request = SearchProductRequest(
            sortBy = ProductSortStrategy.RECOMMENDED,
            searchContext = SearchProductContext(
                userId = 1L,
            ),
        )

        val response = filter.filter(request, products)
        assertEquals(listOf(11L, 12L, 10L, 13L), response.map { it.id })
    }
}