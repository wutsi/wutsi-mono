package com.wutsi.blog.client.follower

data class CountFollowerResponse(
    val counts: List<FollowerCountDto> = emptyList(),
)
