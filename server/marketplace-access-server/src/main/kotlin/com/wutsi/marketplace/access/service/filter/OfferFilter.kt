package com.wutsi.marketplace.access.service.filter

import com.wutsi.marketplace.access.dto.OfferSummary

interface OfferFilter {
    fun filter(offers: List<OfferSummary>): List<OfferSummary>
}
