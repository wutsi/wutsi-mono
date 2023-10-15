package com.wutsi.blog.subscription.dao

import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.util.Predicates

class SearchSubscriptionQueryBuilder {
    fun query(request: SearchSubscriptionRequest): String {
        val select = select()
        val from = from()
        val where = where(request)
        val orderBy = orderBy()
        val limit = limit(request)
        val offset = offset(request)

        return "$select $from $where $orderBy $limit $offset"
    }

    fun count(request: SearchSubscriptionRequest): String {
        val from = from()
        val where = where(request)

        return "SELECT count(*) $from $where"
    }

    fun parameters(request: SearchSubscriptionRequest): Array<Any> {
        return Predicates.parameters(
            request.userIds,
            request.subscriberId,
        )
    }

    private fun select() = "SELECT *"

    private fun from() = "FROM T_SUBSCRIPTION"

    private fun where(request: SearchSubscriptionRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.`in`("user_fk", request.userIds))
        predicates.add(Predicates.eq("subscriber_fk", request.subscriberId))

        return Predicates.where(predicates)
    }

    private fun orderBy() = "ORDER BY timestamp DESC"

    private fun limit(request: SearchSubscriptionRequest) = "LIMIT ${request.limit}"

    private fun offset(request: SearchSubscriptionRequest) = "OFFSET ${request.offset}"
}
