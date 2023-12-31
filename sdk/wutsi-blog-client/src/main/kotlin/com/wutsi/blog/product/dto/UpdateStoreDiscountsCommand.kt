package com.wutsi.blog.product.dto

data class UpdateStoreDiscountsCommand(
    val storeId: String = "",
    val subscriberDiscount: Int = 0,
    val firstPurchaseDiscount: Int = 0,
    val nextPurchaseDiscount: Int = 0,
    val nextPurchaseDiscountDays: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
)
