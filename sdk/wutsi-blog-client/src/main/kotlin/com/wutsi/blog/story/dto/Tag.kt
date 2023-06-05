package com.wutsi.blog.story.dto

data class Tag(
    val id: Long = -1,
    val name: String = "",
    val displayName: String = "",
    val totalStories: Long = 0,
)
