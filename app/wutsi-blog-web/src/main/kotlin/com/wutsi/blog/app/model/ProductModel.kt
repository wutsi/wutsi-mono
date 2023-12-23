package com.wutsi.blog.app.model

import com.wutsi.blog.app.util.NumberUtils

data class ProductModel(
    val id: Long = -1,
    val storeId: String = "",
    val title: String = "",
    val description: String? = null,
    val price: MoneyModel = MoneyModel(),
    val available: Boolean = true,
    val imageUrl: String? = null,
    val thumbnailUrl: String? = null,
    val fileUrl: String? = null,
    val slug: String = "",
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
            "application/epub+zip" -> "epub"
            "application/gzip" -> "gz"
            else -> "bin"
        }
    val fileContentLengthText
        get() = NumberUtils.toHumanReadable(fileContentLength, suffix = "b")
}
