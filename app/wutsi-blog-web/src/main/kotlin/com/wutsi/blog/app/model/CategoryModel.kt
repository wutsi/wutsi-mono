package com.wutsi.blog.app.model

data class CategoryModel(
    val id: Long = 0,
    val parentId: Long? = null,
    val level: Int = 0,
    val title: String = "",
    val longTitle: String = "",
)
