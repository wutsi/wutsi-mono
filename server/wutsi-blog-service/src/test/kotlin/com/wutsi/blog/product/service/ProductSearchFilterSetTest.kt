package com.wutsi.blog.product.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.service.filter.PurchasedProductSearchFilter
import com.wutsi.blog.product.service.filter.TaggedProductSearchFilter
import com.wutsi.platform.core.logging.KVLogger
import kotlin.test.Test
import kotlin.test.assertEquals

class ProductSearchFilterSetTest {
    private val purchased = mock<PurchasedProductSearchFilter>()
    private val tagged = mock<TaggedProductSearchFilter>()
    private val logger = mock<KVLogger>()
    private val set = ProductSearchFilterSet(purchased, tagged, logger)
    private val products = listOf(
        ProductEntity(id = 10, title = "Mon amants, mes enfants"),
        ProductEntity(id = 11, title = "Drools II"),
        ProductEntity(id = 12, title = "Les feux de l'amour - Episode 1"),
        ProductEntity(id = 13, title = "Les feux et l'amour - Prologue")
    )

    @Test
    fun filter() {
        doReturn(
            listOf(
                products[3],
                products[0],
                products[1],
                products[2],
            )
        ).whenever(tagged).filter(any(), any())

        doReturn(
            listOf(
                products[3],
                products[0],
                products[2],
                products[1],
            )
        ).whenever(purchased).filter(any(), any())

        val result = set.filter(SearchProductRequest(), products)

        assertEquals(listOf(13L, 10L, 12L, 11L), result.map { it.id })
    }
}