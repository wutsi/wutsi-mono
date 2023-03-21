package com.wutsi.marketplace.access.service.filter

import com.wutsi.marketplace.access.dto.OfferSummary

/**
 * Bubble down out of stock products
 */
class OutOfStockProductFilter : OfferFilter {
    override fun filter(offers: List<OfferSummary>): List<OfferSummary> {
        val result = mutableListOf<OfferSummary>()
        result.addAll(offers.filter { !it.product.outOfStock })
        result.addAll(offers.filter { it.product.outOfStock })
        return result
    }
}
