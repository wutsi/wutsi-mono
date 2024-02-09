package com.wutsi.blog.app.model

data class OfferModel(
    val productId: Long = -1,
    val price: MoneyModel = MoneyModel(),
    val referencePrice: MoneyModel = MoneyModel(),
    val savingAmount: MoneyModel = MoneyModel(),
    val savingPercentage: Int = 0,
    val discount: DiscountModel? = null,
    val internationalPrice: MoneyModel? = null,
) {
    val hasSavings: Boolean
        get() = savingAmount.value > 0
}
