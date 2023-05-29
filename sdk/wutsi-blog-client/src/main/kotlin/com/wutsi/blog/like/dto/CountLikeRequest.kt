package com.wutsi.blog.like.dto

data class CountLikeRequest(
    val storyIds: List<Long> = emptyList(),
    val userId: Long? = null,
    val deviceId: String? = null,
)
