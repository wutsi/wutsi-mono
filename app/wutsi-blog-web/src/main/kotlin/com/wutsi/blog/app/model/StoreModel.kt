package com.wutsi.blog.app.model

data class StoreModel(
    val id: String = "",
    val userId: Long = -1,
    val currency: String = "",
    val productCount: Long = 0,
    val publishProductCount: Long = 0,
    val orderCount: Long = 0,
    val totalSales: Long = 0,
    val subscriberDiscount: Int = 0,
    val firstPurchaseDiscount: Int = 0,
    val nextPurchaseDiscount: Int = 0,
    val nextPurchaseDiscountDays: Int = 0,
    val abandonedOrderDiscount: Int = 0,
    val enableDonationDiscount: Boolean = false,
) {
    val maxDiscount: Int
        get() = listOf(
            subscriberDiscount,
            firstPurchaseDiscount,
            nextPurchaseDiscount,
        ).filter { it > 0 }
            .sortedDescending()
            .first()

    val minDiscount: Int
        get() = listOf(
            subscriberDiscount,
            firstPurchaseDiscount,
            nextPurchaseDiscount,
        ).filter { it > 0 }
            .sorted()
            .first()
}
