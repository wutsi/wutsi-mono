package com.wutsi.blog.user.dto

import com.wutsi.blog.SortOrder
import javax.validation.constraints.NotNull

data class SearchUserRequest(
    @get:NotNull val siteId: Long? = 1L,
    val userIds: List<Long> = emptyList(),
    val limit: Int = 20,
    val offset: Int = 0,
    val sortBy: UserSortStrategy = UserSortStrategy.CREATED,
    val sortOrder: SortOrder = SortOrder.ASCENDING,
    val blog: Boolean? = null,
    val testUser: Boolean? = false,
    val active: Boolean? = null,
)
