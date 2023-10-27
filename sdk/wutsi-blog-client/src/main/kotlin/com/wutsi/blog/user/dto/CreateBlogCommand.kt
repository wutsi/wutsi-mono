package com.wutsi.blog.user.dto

data class CreateBlogCommand(
    val userId: Long = -1,
    val subscriptionUserIds: List<Long> = emptyList(),
)
