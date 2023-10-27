package com.wutsi.blog.user.dto

data class CreateBlogCommand(
    val userId: Long = -1,
    val subscribeToUserIds: List<Long> = listOf(),
)
