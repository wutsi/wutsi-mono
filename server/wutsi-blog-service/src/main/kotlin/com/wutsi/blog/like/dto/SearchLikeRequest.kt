package com.wutsi.blog.like.dto

data class SearchLikeRequest(
    val storyIds: List<Long> = emptyList(),
    val userId: Long? = null,
    val deviceId: String? = null,
)
