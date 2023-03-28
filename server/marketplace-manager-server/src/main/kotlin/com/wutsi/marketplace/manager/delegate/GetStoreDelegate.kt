package com.wutsi.marketplace.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.GetStoreResponse
import com.wutsi.marketplace.manager.dto.Store
import org.springframework.stereotype.Service

@Service
public class GetStoreDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) {
    public fun invoke(id: Long): GetStoreResponse {
        val store = marketplaceAccessApi.getStore(id).store
        return GetStoreResponse(
            store = objectMapper.readValue(
                objectMapper.writeValueAsString(store),
                Store::class.java,
            ),
        )
    }
}
