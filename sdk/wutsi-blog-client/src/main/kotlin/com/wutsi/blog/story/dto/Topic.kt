package com.wutsi.blog.story.dto

data class Topic(
    val id: Long = -1,
    val parentId: Long = -1,
    val name: String = "",
)
