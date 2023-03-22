package com.wutsi.marketplace.manager.dto

public data class Offer(
    public val product: Product = Product(),
    public val price: OfferPrice = OfferPrice(),
)
