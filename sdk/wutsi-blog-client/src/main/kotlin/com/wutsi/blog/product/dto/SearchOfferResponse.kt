package com.wutsi.blog.product.dto

data class SearchOfferResponse(
    val offers: List<Offer> = emptyList()
)
