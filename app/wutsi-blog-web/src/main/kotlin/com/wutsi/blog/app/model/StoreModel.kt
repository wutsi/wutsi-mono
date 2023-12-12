package com.wutsi.blog.app.model

data class StoreModel(
    val id: String = "",
    val country: CountryModel = CountryModel(),
    val currency: String = "",
    val productCount: Long = 0,
    val publishProductCount: Long = 0,
    val orderCount: Long = 0,
    val totalSales: Long = 0,
)
