package com.wutsi.blog.like.dto

data class Like(
    val storyId: Long = -1,
    val count: Long = 0,
    val liked: Boolean = false,
)

data class SearchLikeResponse(
    val likes: List<Like> = emptyList(),
)
