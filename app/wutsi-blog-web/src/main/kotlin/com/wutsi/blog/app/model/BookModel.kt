package com.wutsi.blog.app.model

import java.util.Date

data class BookModel(
    val id: Long = 0,
    val userId: Long = -1,
    val transactionId: String = "",
    val product: ProductModel = ProductModel(),
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
    val location: String? = null,
) {
    val playUrl: String
        get() = "/me/play/$id"
}
