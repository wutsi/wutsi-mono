package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.access.MarketplaceAccessApi
import org.springframework.stereotype.Service

@Service
public class RemoveDiscountProductDelegate(private val marketplaceAccessApi: MarketplaceAccessApi) {
    public fun invoke(discountId: Long, productId: Long) {
        marketplaceAccessApi.removeDiscountProduct(discountId, productId)
    }
}
