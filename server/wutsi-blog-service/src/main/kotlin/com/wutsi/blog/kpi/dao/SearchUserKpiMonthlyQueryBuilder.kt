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
            request.fromDate?.year,
            request.fromDate?.monthValue,
            request.toDate?.year,
            request.toDate?.monthValue,
            0, // source
        )
    }

    private fun select() = "SELECT K.*"

    private fun from(): String =
        "FROM T_USER_KPI K"

    private fun where(request: SearchUserKpiRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.`in`("K.user_id", request.userIds))
        predicates.add(Predicates.`in`("K.type", request.types.map { it.ordinal }))
        predicates.add(Predicates.gte("K.year", request.fromDate?.year))
        predicates.add(Predicates.gte("K.month", request.fromDate?.monthValue))
        predicates.add(Predicates.lte("K.year", request.toDate?.year))
        predicates.add(Predicates.lte("K.month", request.toDate?.monthValue))
        if (request.dimension == Dimension.ALL) {
            predicates.add(Predicates.eq("K.source", 0))
        } else {
            predicates.add(Predicates.gt("K.source", 0))
        }
        return Predicates.where(predicates)
    }

    private fun order(): String =
        "ORDER BY year, month"
}
