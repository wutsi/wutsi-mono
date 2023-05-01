package com.wutsi.blog.client.like

data class SearchLikeResponse(
    val likes: List<LikeDto> = emptyList(),
)
