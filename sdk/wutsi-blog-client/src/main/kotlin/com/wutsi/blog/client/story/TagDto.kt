package com.wutsi.blog.client.story

data class TagDto(
    val id: Long = -1,
    val name: String = "",
    val displayName: String = "",
    val totalStories: Long = 0,
)
