package com.wutsi.blog.app.model

import com.wutsi.blog.product.dto.DiscountType
import java.util.Date

data class DiscountModel(
    val type: DiscountType = DiscountType.UNKNOWN,
    val percentage: Int,
    val expiryDate: Date? = null,
    val expiryDateText: String? = null,
) {
    val subscriber: Boolean
        get() = (type == DiscountType.SUBSCRIBER)

    val firstPurchase: Boolean
        get() = (type == DiscountType.FIRST_PURCHASE)

    val nextPurchase: Boolean
        get() = (type == DiscountType.NEXT_PURCHASE)

    val remainingDays: Long?
        get() = expiryDate?.let { date -> (date.time - System.currentTimeMillis()) / 86400000 }
}
