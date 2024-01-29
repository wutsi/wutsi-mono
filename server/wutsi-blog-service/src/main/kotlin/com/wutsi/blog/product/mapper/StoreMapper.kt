package com.wutsi.blog.product.mapper

import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.Store
import org.springframework.stereotype.Service

@Service
class StoreMapper {
    fun toStore(store: StoreEntity) = Store(
        id = store.id ?: "",
        userId = store.userId,
        currency = store.currency,
        productCount = store.productCount,
        publishProductCount = store.publishProductCount,
        orderCount = store.orderCount,
        totalSales = store.totalSales,
        creationDateTime = store.creationDateTime,
        modificationDateTime = store.modificationDateTime,
        firstPurchaseDiscount = store.firstPurchaseDiscount,
        nextPurchaseDiscountDays = store.nextPurchaseDiscountDays,
        nextPurchaseDiscount = store.nextPurchaseDiscount,
        subscriberDiscount = store.subscriberDiscount,
        abandonedOrderDiscount = store.abandonedOrderDiscount,
        enableDonationDiscount = store.enableDonationDiscount,
    )
}
