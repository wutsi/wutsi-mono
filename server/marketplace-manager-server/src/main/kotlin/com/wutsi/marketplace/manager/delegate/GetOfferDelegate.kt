package com.wutsi.marketplace.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.GetOfferResponse
import com.wutsi.marketplace.manager.dto.Offer
import org.springframework.stereotype.Service

@Service
public class GetOfferDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) {
    public fun invoke(id: Long): GetOfferResponse {
        val offer = marketplaceAccessApi.getOffer(id).offer
        return GetOfferResponse(
            offer = objectMapper.readValue(
                objectMapper.writeValueAsString(offer),
                Offer::class.java,
            ),
        )
    }
}
