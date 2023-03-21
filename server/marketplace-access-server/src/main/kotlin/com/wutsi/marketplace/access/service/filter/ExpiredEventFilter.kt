package com.wutsi.marketplace.access.service.filter

import com.wutsi.enums.ProductType
import com.wutsi.marketplace.access.dto.OfferSummary
import java.time.OffsetDateTime

/**
 * Remove all expired events
 */
class ExpiredEventFilter : OfferFilter {
    override fun filter(offers: List<OfferSummary>): List<OfferSummary> =
        offers.filter { isNotExpired(it) }

    private fun isNotExpired(offer: OfferSummary): Boolean =
        offer.product.type != ProductType.EVENT.name ||
            offer.product.event?.ends == null ||
            offer.product.event.ends.isAfter(OffsetDateTime.now())
}
