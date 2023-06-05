package com.wutsi.blog.account.dao

import com.wutsi.blog.SortOrder
import com.wutsi.blog.client.user.SearchUserRequest
import com.wutsi.blog.client.user.UserSortStrategy
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
            request.siteId,
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
        predicates.add(Predicates.eq("site_id", request.siteId))

        return Predicates.where(predicates)
    }

    private fun order(request: SearchUserRequest): String {
        val order = if (request.sortOrder == SortOrder.DESCENDING) "DESC" else "ASC"
        if (request.sortBy == UserSortStrategy.created) {
            return "ORDER BY id $order"
        } else if (request.sortBy == UserSortStrategy.stories) {
            return "ORDER BY story_count $order"
        } else if (request.sortBy == UserSortStrategy.followers) {
            return "ORDER BY follower_count $order"
        } else if (request.sortBy == UserSortStrategy.last_publication) {
            return "ORDER BY last_publication_date_time $order"
        } else {
            return ""
        }
    }

    private fun limit(request: SearchUserRequest) = "LIMIT ${request.limit}"

    private fun offset(request: SearchUserRequest) = "OFFSET ${request.offset}"
}
