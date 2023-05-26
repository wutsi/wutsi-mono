package com.wutsi.blog.like.dto

data class SearchLikeResponse(
    val likes: List<Like> = emptyList(),
)
