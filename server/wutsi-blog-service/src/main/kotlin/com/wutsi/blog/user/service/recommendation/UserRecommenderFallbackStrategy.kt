package com.wutsi.blog.user.service.recommendation

import com.wutsi.blog.SortOrder
import com.wutsi.blog.user.dto.RecommendUserRequest
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.UserSortStrategy
import com.wutsi.blog.user.service.UserRecommenderStrategy
import com.wutsi.blog.user.service.UserService
import org.springframework.stereotype.Service

@Service
class UserRecommenderFallbackStrategy(
    private val service: UserService,
) : UserRecommenderStrategy {
    override fun recommend(request: RecommendUserRequest): List<Long> =
        service.search(
            SearchUserRequest(
                excludeUserIds = request.readerId?.let { listOf(it) } ?: emptyList(),
                blog = true,
                withPublishedStories = true,
                active = true,
                limit = request.limit,
                sortBy = UserSortStrategy.POPULARITY,
                sortOrder = SortOrder.DESCENDING,
            ),
        )
            .map { it.id!! }
}
