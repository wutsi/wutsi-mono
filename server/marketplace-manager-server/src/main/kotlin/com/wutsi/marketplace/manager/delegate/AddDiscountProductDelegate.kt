package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.access.MarketplaceAccessApi
import org.springframework.stereotype.Service

@Service
public class AddDiscountProductDelegate(private val marketplaceAccessApi: MarketplaceAccessApi) {
    public fun invoke(discountId: Long, productId: Long) {
        marketplaceAccessApi.addDiscountProduct(discountId, productId)
    }
}
