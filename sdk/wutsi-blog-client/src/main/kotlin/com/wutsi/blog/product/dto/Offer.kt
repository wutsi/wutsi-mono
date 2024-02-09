package com.wutsi.blog.product.dto

data class Offer(
    val productId: Long = -1,
    val price: Long = 0,
    val referencePrice: Long = 0,
    val savingAmount: Long = 0,
    val savingPercentage: Int = 0,
    val discount: Discount? = null,
    val internationalPrice: Long? = null,
    val internationalCurrency: String? = null,
)
