package com.wutsi.blog.like.dto

data class CountLikeResponse(
    val counters: List<LikeCounter> = emptyList(),
)
