package com.wutsi.marketplace.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.GetProductResponse
import com.wutsi.marketplace.manager.dto.Product
import org.springframework.stereotype.Service

@Service
public class GetProductDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) {
    public fun invoke(id: Long): GetProductResponse {
        val product = marketplaceAccessApi.getProduct(id).product
        return GetProductResponse(
            product = objectMapper.readValue(
                objectMapper.writeValueAsString(product),
                Product::class.java,
            ),
        )
    }
}
