package com.wutsi.blog.client.like

data class LikeCountDto(
    val storyId: Long = -1,
    val value: Long = 0,
)
