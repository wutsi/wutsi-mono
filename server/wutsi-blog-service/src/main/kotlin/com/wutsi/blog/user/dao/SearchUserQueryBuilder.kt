package com.wutsi.blog.user.dao

import com.wutsi.blog.SortOrder
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.UserSortStrategy
import com.wutsi.blog.util.Predicates

class SearchUserQueryBuilder {
    fun query(request: SearchUserRequest): String {
        val select = select()
        val from = from()
        val where = where(request)
        val order = order(request)
        val limit = limit(request)
        val offset = offset(request)

        return "$select $from $where $order $limit $offset"
    }

    fun count(request: SearchUserRequest): String {
        val from = from()
        val where = where(request)

        return "SELECT count(*) $from $where"
    }

    fun parameters(request: SearchUserRequest): Array<Any> {
        return Predicates.parameters(
            false, // suspended
            request.userIds,
            request.blog,
            request.testUser,
            request.active,
            request.withPublishedStories?.let { "0" }
        )
    }

    private fun select() = "SELECT *"

    private fun from() = "FROM T_USER"

    private fun where(request: SearchUserRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.eq("suspended", false))
        predicates.add(Predicates.`in`("id", request.userIds))
        predicates.add(Predicates.eq("blog", request.blog))
        predicates.add(Predicates.eq("test_user", request.testUser))
        predicates.add(Predicates.eq("active", request.active))

        request.withPublishedStories?.let { value ->
            if (value) {
                predicates.add(Predicates.gt("publish_story_count", "0"))
            } else {
                predicates.add(Predicates.eq("publish_story_count", "0"))
            }
        }
        return Predicates.where(predicates)
    }

    private fun order(request: SearchUserRequest): String {
        val order = if (request.sortOrder == SortOrder.DESCENDING) "DESC" else "ASC"
        return when (request.sortBy) {
            UserSortStrategy.CREATED -> "ORDER BY id $order"
            UserSortStrategy.STORY_COUNT -> "ORDER BY story_count $order"
            UserSortStrategy.SUBSCRIBER_COUNT -> "ORDER BY follower_count $order"
            UserSortStrategy.LAST_PUBLICATION -> "ORDER BY last_publication_date_time $order"
            UserSortStrategy.POPULARITY -> "ORDER BY active DESC, read_count $order"
            else -> ""
        }
    }

    private fun limit(request: SearchUserRequest) = "LIMIT ${request.limit}"

    private fun offset(request: SearchUserRequest) = "OFFSET ${request.offset}"
}
