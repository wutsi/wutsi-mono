package com.wutsi.blog.product.dto

import java.util.Date

data class Store(
    val id: String = "",
    val userId: Long = -1,
    val currency: String = "",
    val productCount: Long = 0,
    val publishProductCount: Long = 0,
    val orderCount: Long = 0,
    val totalSales: Long = 0,
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
    val subscriberDiscount: Int = 0,
    val firstPurchaseDiscount: Int = 0,
    val nextPurchaseDiscount: Int = 0,
    val nextPurchaseDiscountDays: Int = 0,
    val abandonedOrderDiscount: Int = 0,
    val enableDonationDiscount: Boolean = false,
)
