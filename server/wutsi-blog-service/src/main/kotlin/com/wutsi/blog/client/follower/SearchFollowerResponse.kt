package com.wutsi.blog.client.follower

data class SearchFollowerResponse(
    val followers: List<FollowerDto> = emptyList(),
)
