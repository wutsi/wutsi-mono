package com.wutsi.blog.client.like

data class CountLikeResponse(
    val counts: List<LikeCountDto> = emptyList(),
)
