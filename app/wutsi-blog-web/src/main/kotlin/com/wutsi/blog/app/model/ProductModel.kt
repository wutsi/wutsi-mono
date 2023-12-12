package com.wutsi.blog.app.model

data class ProductModel(
    val id: Long = -1,
    val title: String = "",
    val description: String? = null,
    val price: PriceModel = PriceModel(),
    val outOfStock: Boolean = false,
    val imageUrl: String? = null,
    val thumbnailUrl: String? = null,
    val store: StoreModel = StoreModel(),
)
