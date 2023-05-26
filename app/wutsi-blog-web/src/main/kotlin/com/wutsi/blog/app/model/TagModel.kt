package com.wutsi.blog.app.model

data class TagModel(
    val id: Long = -1,
    val name: String = "",
    val displayName: String = "",
    val totalStories: Long = 0,
)
