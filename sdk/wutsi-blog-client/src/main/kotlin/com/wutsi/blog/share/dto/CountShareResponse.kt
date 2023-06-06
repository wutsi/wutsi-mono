package com.wutsi.blog.share.dto

data class CountShareResponse(
    val counters: List<ShareCounter> = emptyList(),
)
