package com.wutsi.marketplace.access.service.filter

import com.wutsi.marketplace.access.dto.OfferSummary
import com.wutsi.marketplace.access.dto.ProductSummary
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class OutOfStockOfferFilterTest {
    private val filter = OutOfStockProductFilter()

    @Test
    fun filter() {
        val p1 = OfferSummary(ProductSummary(id = 1, outOfStock = true))
        val p2 = OfferSummary(ProductSummary(id = 2, outOfStock = false))
        val p3 = OfferSummary(ProductSummary(id = 3, outOfStock = false))

        val result = filter.filter(listOf(p1, p2, p3))

        assertEquals(listOf(p2, p3, p1), result)
    }
}
