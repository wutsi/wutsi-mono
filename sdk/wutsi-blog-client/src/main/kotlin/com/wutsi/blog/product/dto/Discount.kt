package com.wutsi.blog.product.dto

import java.util.Date

data class Discount(
    val type: DiscountType = DiscountType.UNKNOWN,
    val percentage: Int,
    val expiryDate: Date? = null,
)
