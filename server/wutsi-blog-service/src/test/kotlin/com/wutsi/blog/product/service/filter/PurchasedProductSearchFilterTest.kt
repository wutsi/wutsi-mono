package com.wutsi.blog.product.service.filter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.SearchProductContext
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.domain.TransactionEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PurchasedProductSearchFilterTest {
    private val transactionDao = mock<TransactionRepository>()
    private val filter = PurchasedProductSearchFilter(transactionDao)
    private val products = listOf(
        ProductEntity(id = 10, title = "Mon amants, mes enfants"),
        ProductEntity(id = 11, title = "Drools II"),
        ProductEntity(id = 12, title = "Les feux de l'amour - Episode 1"),
        ProductEntity(id = 13, title = "Les feux et l'amour - Prologue")
    )

    @Test
    fun `no current user id`() {
        val request = SearchProductRequest(
            bubbleDownPurchasedProduct = true,
            searchContext = SearchProductContext(userId = null),
        )
        val response = filter.filter(request, products)
        assertEquals(products.map { it.id }, response.map { it.id })
    }

    @Test
    fun `do not bubble down`() {
        val request = SearchProductRequest(
            bubbleDownPurchasedProduct = false,
            searchContext = SearchProductContext(userId = 11L),
        )
        val response = filter.filter(request, products)
        assertEquals(products.map { it.id }, response.map { it.id })
    }

    @Test
    fun `no purcharse`() {
        val txs = listOf(
            TransactionEntity(),
            TransactionEntity(),
            TransactionEntity(),
        )
        doReturn(txs).whenever(transactionDao).findByUserIdByTypeByStatus(any(), any(), any())

        val request = SearchProductRequest(
            bubbleDownPurchasedProduct = true,
            searchContext = SearchProductContext(userId = 11L),
        )
        val response = filter.filter(request, products)
        assertEquals(products.map { it.id }, response.map { it.id })
    }

    @Test
    fun filter() {
        val txs = listOf(
            TransactionEntity(product = ProductEntity(id = 10)),
            TransactionEntity(product = ProductEntity(id = 12)),
            TransactionEntity(product = ProductEntity(id = 101)),
            TransactionEntity(product = ProductEntity(id = 102)),
            TransactionEntity(product = null),
        )
        doReturn(txs).whenever(transactionDao).findByUserIdByTypeByStatus(any(), any(), any())

        val request = SearchProductRequest(
            bubbleDownPurchasedProduct = true,
            searchContext = SearchProductContext(userId = 11L),
        )
        val response = filter.filter(request, products)

        assertEquals(listOf(11L, 13L, 10L, 12L), response.map { it.id })
    }
}