package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.UpdateFundraisingAttributeRequest
import org.springframework.stereotype.Service

@Service
public class UpdateFundraisingAttributeDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
) {
    public fun invoke(id: Long, request: UpdateFundraisingAttributeRequest) {
        marketplaceAccessApi.updateFundraisingAttribute(
            id = id,
            request = com.wutsi.marketplace.access.dto.UpdateFundraisingAttributeRequest(
                name = request.name,
                value = request.value,
            ),
        )
    }
}
