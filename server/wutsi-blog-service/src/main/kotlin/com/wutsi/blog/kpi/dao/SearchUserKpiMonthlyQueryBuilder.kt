package com.wutsi.blog.kpi.dao

import com.wutsi.blog.kpi.dto.Dimension
import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import com.wutsi.blog.util.Predicates

class SearchUserKpiMonthlyQueryBuilder {
    fun query(request: SearchUserKpiRequest): String {
        val select = select()
        val from = from()
        val where = where(request)
        val order = order()

        return "$select $from $where $order"
    }

    fun parameters(request: SearchUserKpiRequest): Array<Any> {
        return Predicates.parameters(
            request.userIds,
            request.types.map { it.ordinal },
            0, // source
        )
    }

    private fun select() = "SELECT *"

    private fun from(): String =
        "FROM T_USER_KPI"

    private fun where(request: SearchUserKpiRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.`in`("user_id", request.userIds))
        predicates.add(Predicates.`in`("type", request.types.map { it.ordinal }))
        if (request.dimension == Dimension.ALL) {
            predicates.add(Predicates.eq("source", 0))
        } else {
            predicates.add(Predicates.gt("source", 0))
        }
        return Predicates.where(predicates)
    }

    private fun order(): String =
        "ORDER BY year, month"
}
