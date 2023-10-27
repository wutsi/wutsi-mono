package com.wutsi.blog.user.dto

data class BlogCreateEventPayload(
    val subscribeToUserIds: List<Long> = listOf(),
)
