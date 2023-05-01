package com.wutsi.blog.client.user

data class SearchUserResponse(
    val users: List<UserSummaryDto> = emptyList(),
)
