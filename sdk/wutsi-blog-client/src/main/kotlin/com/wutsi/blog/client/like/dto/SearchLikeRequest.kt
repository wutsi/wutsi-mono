package com.wutsi.blog.client.like.dto

data class SearchLikeRequest(
    val storyIds: List<Long> = emptyList(),
    val userId: Long? = null,
    val deviceId: String? = null,
)
