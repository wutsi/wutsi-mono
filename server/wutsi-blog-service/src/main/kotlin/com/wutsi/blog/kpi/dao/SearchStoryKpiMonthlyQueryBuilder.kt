package com.wutsi.blog.kpi.dao

import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.util.Predicates

class SearchStoryKpiMonthlyQueryBuilder {
    fun query(request: SearchStoryKpiRequest): String {
        val select = select()
        val from = from()
        val where = where(request)
        val order = order()

        return "$select $from $where $order"
    }

    fun parameters(request: SearchStoryKpiRequest): Array<Any> {
        return Predicates.parameters(
            request.storyIds,
            request.types.map { it.ordinal },
        )
    }

    private fun select() = "SELECT *"

    private fun from(): String =
        "FROM T_STORY_KPI"

    private fun where(request: SearchStoryKpiRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.`in`("story_id", request.storyIds))
        predicates.add(Predicates.`in`("type", request.types.map { it.ordinal }))
        return Predicates.where(predicates)
    }

    private fun order(): String =
        "ORDER BY year, month"
}
