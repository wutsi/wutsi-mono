package com.wutsi.marketplace.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.Discount
import com.wutsi.marketplace.manager.dto.GetDiscountResponse
import org.springframework.stereotype.Service

@Service
public class GetDiscountDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) {
    public fun invoke(id: Long): GetDiscountResponse {
        val discount = marketplaceAccessApi.getDiscount(id).discount
        return GetDiscountResponse(
            discount = objectMapper.readValue(
                objectMapper.writeValueAsString(discount),
                Discount::class.java,
            ),
        )
    }
}
