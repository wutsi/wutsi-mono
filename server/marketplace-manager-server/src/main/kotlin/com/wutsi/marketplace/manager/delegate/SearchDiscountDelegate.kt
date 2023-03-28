package com.wutsi.marketplace.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.DiscountSummary
import com.wutsi.marketplace.manager.dto.SearchDiscountRequest
import com.wutsi.marketplace.manager.dto.SearchDiscountResponse
import org.springframework.stereotype.Service

@Service
public class SearchDiscountDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) {
    public fun invoke(request: SearchDiscountRequest): SearchDiscountResponse {
        val discounts = marketplaceAccessApi.searchDiscount(
            request = com.wutsi.marketplace.access.dto.SearchDiscountRequest(
                storeId = request.storeId,
                date = request.date,
                productIds = request.productIds,
                discountIds = request.discountIds,
                limit = request.limit,
                offset = request.offset,
                type = request.type,
            ),
        ).discounts
        return SearchDiscountResponse(
            discounts = discounts.map {
                objectMapper.readValue(
                    objectMapper.writeValueAsString(it),
                    DiscountSummary::class.java,
                )
            },
        )
    }
}
