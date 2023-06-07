package com.wutsi.blog.user.dto

data class SearchUserResponse(
    val users: List<UserSummary> = emptyList(),
)
