package com.wutsi.blog.app.form

data class StoreDiscountsForm(
    val subscriberDiscount: Int = 0,
    val firstPurchaseDiscount: Int = 0,
    val nextPurchaseDiscount: Int = 0,
    val nextPurchaseDiscountDays: Int = 0,
    val enableDonationDiscount: Boolean = false,
    val abandonedOrderDiscount: Int = 0,
)
