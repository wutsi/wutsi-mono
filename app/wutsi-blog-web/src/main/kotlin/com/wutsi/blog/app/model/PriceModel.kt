package com.wutsi.blog.app.model

data class PriceModel(
    val priceText: String = "",
    val amount: Long = 0,
    val currency: String = "",
) {
    override fun toString(): String {
        return priceText
    }
}
