package com.wutsi.blog.app.model

import com.wutsi.blog.app.util.NumberUtils

data class ProductModel(
    val id: Long = -1,
    val title: String = "",
    val description: String? = null,
    val price: PriceModel = PriceModel(),
    val available: Boolean = true,
    val imageUrl: String? = null,
    val thumbnailUrl: String? = null,
    val fileUrl: String? = null,
    val slug: String = "",
    val store: StoreModel = StoreModel(),
    val url: String = "",
    val orderCount: Long = 0,
    val totalSales: Long = 0,
    val fileContentLength: Long = 0,
    val fileContentType: String? = null,
) {
    val fileExtension
        get() = when (fileContentType) {
            "text/plain" -> "txt"
            "application/pdf" -> "pdf"
            else -> "bin"
        }
    val fileContentLengthText
        get() = NumberUtils.toHumanReadable(fileContentLength)
}