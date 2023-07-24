package com.wutsi.blog.kpi.dao

import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.util.Predicates

class SearchStoryKpiMonthlyQueryBuilder {
    fun query(request: SearchStoryKpiRequest): String {
        val select = select()
        val from = from(request)
        val where = where(request)
        val order = order()

        return "$select $from $where $order"
    }

    fun parameters(request: SearchStoryKpiRequest): Array<Any> {
        return Predicates.parameters(
            request.storyIds,
            request.types.map { it.ordinal },
            request.userId,
            request.fromDate?.year,
            request.fromDate?.monthValue,
            request.toDate?.year,
            request.toDate?.monthValue,
        )
    }

    private fun select() = "SELECT *"

    private fun from(request: SearchStoryKpiRequest): String =
        if (request.userId == null) {
            "FROM T_STORY_KPI K"
        } else {
            "FROM T_STORY_KPI K JOIN T_STORY S ON K.story_id=S.id"
        }

    private fun where(request: SearchStoryKpiRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.`in`("K.story_id", request.storyIds))
        predicates.add(Predicates.`in`("K.type", request.types.map { it.ordinal }))
        predicates.add(Predicates.eq("S.user_fk", request.userId))
        predicates.add(Predicates.gte("K.year", request.fromDate?.year))
        predicates.add(Predicates.gte("K.year", request.fromDate?.monthValue))
        predicates.add(Predicates.lte("K.year", request.toDate?.year))
        predicates.add(Predicates.lte("K.year", request.toDate?.monthValue))
        return Predicates.where(predicates)
    }

    private fun order(): String =
        "ORDER BY year, month"
}
