package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.UpdateDiscountAttributeRequest
import org.springframework.stereotype.Service

@Service
public class UpdateDiscountAttributeDelegate(private val marketplaceAccessApi: MarketplaceAccessApi) {
    public fun invoke(id: Long, request: UpdateDiscountAttributeRequest) {
        marketplaceAccessApi.updateDiscountAttribute(
            id,
            com.wutsi.marketplace.access.dto.UpdateDiscountAttributeRequest(
                name = request.name,
                value = request.value,
            ),
        )
    }
}
