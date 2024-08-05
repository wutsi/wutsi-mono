package com.wutsi.blog.transaction.dao

import com.wutsi.blog.transaction.dto.SearchSuperFanRequest
import com.wutsi.blog.util.Predicates

class SearchSuperFanQueryBuilder {
    fun query(request: SearchSuperFanRequest): String {
        val select = select()
        val from = from()
        val where = where(request)
        val limit = limit(request)
        val offset = offset(request)
        val order = orderBy()

        return "$select $from $where $order $limit $offset"
    }

    fun parameters(request: SearchSuperFanRequest): Array<Any> = Predicates.parameters(
            request.walletId,
            request.userId,
        )

    private fun select() = "SELECT DISTINCT *"

    private fun from() = "FROM V_SUPER_FAN"

    private fun where(request: SearchSuperFanRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.eq("wallet_id", request.walletId))
        predicates.add(Predicates.eq("user_id", request.userId))
        return Predicates.where(predicates)
    }

    private fun orderBy(): String =
        "ORDER BY value DESC"

    private fun limit(request: SearchSuperFanRequest) = "LIMIT ${request.limit}"

    private fun offset(request: SearchSuperFanRequest) = "OFFSET ${request.offset}"
}
