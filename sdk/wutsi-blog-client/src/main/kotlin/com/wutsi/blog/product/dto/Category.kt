package com.wutsi.blog.product.dto

data class Category(
    val id: Long = 0,
    val parentId: Long? = null,
    val title: String = "",
    val titleFrench: String? = null,
    val titleFrenchAscii: String? = null,
    val level: Int = 0,
    val longTitle: String = "",
    val longTitleFrench: String? = null,
)
