package com.wutsi.marketplace.access.service.filter

import com.wutsi.marketplace.access.dto.OfferSummary

class OfferSetFilter(
    private val filters: List<OfferFilter>,
) : OfferFilter {
    override fun filter(offers: List<OfferSummary>): List<OfferSummary> {
        var result = offers
        filters.forEach {
            result = it.filter(result)
        }
        return result
    }
}
