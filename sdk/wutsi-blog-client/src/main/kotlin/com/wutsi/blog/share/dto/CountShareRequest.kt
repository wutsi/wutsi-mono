package com.wutsi.blog.share.dto

data class CountShareRequest(
    val storyIds: List<Long> = emptyList(),
)
