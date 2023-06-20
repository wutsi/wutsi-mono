package com.wutsi.blog.user.dto

import com.wutsi.blog.SortOrder

data class SearchUserRequest(
    val userIds: List<Long> = emptyList(),
    val excludeUserIds: List<Long> = emptyList(),
    val limit: Int = 20,
    val offset: Int = 0,
    val sortBy: UserSortStrategy = UserSortStrategy.CREATED,
    val sortOrder: SortOrder = SortOrder.ASCENDING,
    val blog: Boolean? = null,
    val testUser: Boolean? = null,
    val active: Boolean? = null,
    val withPublishedStories: Boolean? = null,
)
