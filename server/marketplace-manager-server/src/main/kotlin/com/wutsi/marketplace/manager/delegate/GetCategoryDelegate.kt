package com.wutsi.marketplace.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.Category
import com.wutsi.marketplace.manager.dto.GetCategoryResponse
import org.springframework.stereotype.Service

@Service
public class GetCategoryDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) {
    public fun invoke(id: Long): GetCategoryResponse {
        val discount = marketplaceAccessApi.getCategory(id).category
        return GetCategoryResponse(
            category = objectMapper.readValue(
                objectMapper.writeValueAsString(discount),
                Category::class.java,
            ),
        )
    }
}
