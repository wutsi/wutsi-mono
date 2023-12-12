package com.wutsi.blog.app.model

data class StoreModel(
    val id: String = "",
    val userId: Long = -1,
    val currency: String = "",
    val productCount: Long = 0,
    val publishProductCount: Long = 0,
    val orderCount: Long = 0,
    val totalSales: Long = 0,
)
