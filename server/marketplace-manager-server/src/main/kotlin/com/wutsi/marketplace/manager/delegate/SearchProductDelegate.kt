package com.wutsi.marketplace.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.ProductSummary
import com.wutsi.marketplace.manager.dto.SearchProductRequest
import com.wutsi.marketplace.manager.dto.SearchProductResponse
import org.springframework.stereotype.Service

@Service
public class SearchProductDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) {
    public fun invoke(request: SearchProductRequest): SearchProductResponse {
        val response = marketplaceAccessApi.searchProduct(
            request = com.wutsi.marketplace.access.dto.SearchProductRequest(
                limit = request.limit,
                offset = request.offset,
                categoryIds = request.categoryIds,
                productIds = request.productIds,
                storeId = request.storeId,
                sortBy = request.sortBy,
                status = request.status,
                types = request.types,
            ),
        )
        return SearchProductResponse(
            products = response.products.map {
                objectMapper.readValue(
                    objectMapper.writeValueAsString(it),
                    ProductSummary::class.java,
                )
            },
        )
    }
}
