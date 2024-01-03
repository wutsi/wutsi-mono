package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.StoreModel
import com.wutsi.blog.product.dto.Store
import org.springframework.stereotype.Service

@Service
class StoreMapper {
    fun toStoreModel(store: Store) = StoreModel(
        id = store.id,
        userId = store.userId,
        currency = store.currency,
        totalSales = store.totalSales,
        orderCount = store.orderCount,
        productCount = store.productCount,
        publishProductCount = store.publishProductCount,
        firstPurchaseDiscount = store.firstPurchaseDiscount,
        nextPurchaseDiscountDays = store.nextPurchaseDiscountDays,
        nextPurchaseDiscount = store.nextPurchaseDiscount,
        subscriberDiscount = store.subscriberDiscount,
    )
}
