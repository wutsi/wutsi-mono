package com.wutsi.blog.follower.dao

import com.wutsi.blog.client.follower.SearchFollowerRequest
import com.wutsi.blog.util.Predicates

class SearchFollowerQueryBuilder {
    fun query(request: SearchFollowerRequest): String {
        val select = select()
        val from = from()
        val where = where(request)
        val limit = limit(request)
        val offset = offset(request)

        return "$select $from $where $limit $offset"
    }

    fun count(request: SearchFollowerRequest): String {
        val from = from()
        val where = where(request)

        return "SELECT user_fk, count(*) $from $where GROUP BY user_fk"
    }

    fun parameters(request: SearchFollowerRequest): Array<Any> {
        return Predicates.parameters(
            request.userId,
            request.followerUserId,
        )
    }

    private fun select() = "SELECT *"

    private fun from(): String =
        "FROM T_FOLLOWER L"

    private fun where(request: SearchFollowerRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.eq("user_fk", request.userId))
        predicates.add(Predicates.eq("follower_user_fk", request.followerUserId))

        return Predicates.where(predicates)
    }

    private fun limit(request: SearchFollowerRequest) = "LIMIT ${request.limit}"

    private fun offset(request: SearchFollowerRequest) = "OFFSET ${request.offset}"
}
