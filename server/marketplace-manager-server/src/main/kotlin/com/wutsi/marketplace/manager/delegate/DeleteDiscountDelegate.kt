package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.access.MarketplaceAccessApi
import org.springframework.stereotype.Service

@Service
public class DeleteDiscountDelegate(private val marketplaceAccessApi: MarketplaceAccessApi) {
    public fun invoke(id: Long) {
        marketplaceAccessApi.deleteDiscount(id)
    }
}
