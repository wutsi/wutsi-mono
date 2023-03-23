package com.wutsi.marketplace.manager.dto

public data class OfferSummary(
    public val product: ProductSummary = ProductSummary(),
    public val price: OfferPrice = OfferPrice(),
)
