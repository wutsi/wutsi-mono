package com.wutsi.blog.product.service.filter

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.SearchProductRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DedupUserProductSearchFilterTest {
    private val filter = DedupUserProductSearchFilter()

    private val products = listOf(
        ProductEntity(id = 10, store = StoreEntity(id = "100")),
        ProductEntity(id = 11, store = StoreEntity(id = "100")),
        ProductEntity(id = 20, store = StoreEntity(id = "200")),
        ProductEntity(id = 30, store = StoreEntity(id = "300"))
    )

    @Test
    fun `no dedup`() {
        val request = SearchProductRequest(
            dedupUser = false
        )

        val response = filter.filter(request, products)
        assertEquals(products.map { it.id }, response.map { it.id })
    }

    @Test
    fun filter() {
        val request = SearchProductRequest(
            dedupUser = true
        )

        val response = filter.filter(request, products)
        assertEquals(listOf(10L, 20L, 30L), response.map { it.id })
    }
}