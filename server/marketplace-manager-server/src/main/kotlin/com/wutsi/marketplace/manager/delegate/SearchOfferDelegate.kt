package com.wutsi.marketplace.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.OfferSummary
import com.wutsi.marketplace.manager.dto.SearchOfferRequest
import com.wutsi.marketplace.manager.dto.SearchOfferResponse
import org.springframework.stereotype.Service

@Service
public class SearchOfferDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) {
    public fun invoke(request: SearchOfferRequest): SearchOfferResponse {
        val response = marketplaceAccessApi.searchOffer(
            request = com.wutsi.marketplace.access.dto.SearchOfferRequest(
                limit = request.limit,
                offset = request.offset,
                storeId = request.storeId,
                sortBy = request.sortBy,
                productIds = request.productIds,
                types = request.types,
            ),
        )
        return SearchOfferResponse(
            offers = response.offers.map {
                objectMapper.readValue(
                    objectMapper.writeValueAsString(it),
                    OfferSummary::class.java,
                )
            },
        )
    }
}
