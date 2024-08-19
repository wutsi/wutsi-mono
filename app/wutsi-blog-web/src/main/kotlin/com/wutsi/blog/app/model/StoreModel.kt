package com.wutsi.blog.app.model

import com.wutsi.blog.app.util.NumberUtils
import java.text.DecimalFormat

data class StoreModel(
    val id: String = "",
    val userId: Long = -1,
    val currency: String = "",
    val productCount: Long = 0,
    val publishProductCount: Long = 0,
    val orderCount: Long = 0,
    val totalSales: MoneyModel = MoneyModel(),
    val subscriberDiscount: Int = 0,
    val firstPurchaseDiscount: Int = 0,
    val nextPurchaseDiscount: Int = 0,
    val nextPurchaseDiscountDays: Int = 0,
    val abandonedOrderDiscount: Int = 0,
    val enableDonationDiscount: Boolean = false,
    val viewCount: Long = 0,
    val cvr: Double = 0.0,
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

    val viewCountText: String
        get() = NumberUtils.toHumanReadable(viewCount)

    val orderCountText: String
        get() = NumberUtils.toHumanReadable(orderCount)

    val totalSalesText: String
        get() = NumberUtils.toHumanReadable(totalSales.value)

    val cvrText: String
        get() = DecimalFormat("0.00").format(100.0 * cvr) + "%"
}
