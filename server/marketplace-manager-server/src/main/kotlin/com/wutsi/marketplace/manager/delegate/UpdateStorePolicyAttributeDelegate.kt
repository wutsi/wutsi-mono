package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.UpdateStorePolicyAttributeRequest
import org.springframework.stereotype.Service

@Service
public class UpdateStorePolicyAttributeDelegate(private val marketplaceAccessApi: MarketplaceAccessApi) {
    public fun invoke(id: Long, request: UpdateStorePolicyAttributeRequest) {
        update(id, request)
    }

    private fun update(id: Long, request: UpdateStorePolicyAttributeRequest) {
        marketplaceAccessApi.updateStorePolicyAttribute(
            id,
            com.wutsi.marketplace.access.dto.UpdateStorePolicyAttributeRequest(
                name = request.name,
                value = request.value,
            ),
        )
    }
}
