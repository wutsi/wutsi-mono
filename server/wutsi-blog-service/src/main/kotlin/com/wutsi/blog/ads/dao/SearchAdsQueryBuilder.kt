package com.wutsi.blog.ads.dao

import com.wutsi.blog.SortOrder
import com.wutsi.blog.ads.dto.AdsSortStrategy
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.util.Predicates

class SearchAdsQueryBuilder {
    fun query(request: SearchAdsRequest): String {
        val select = select()
        val from = from()
        val where = where(request)
        val limit = limit(request)
        val offset = offset(request)
        val order = order(request)

        return "$select $from $where $order $limit $offset"
    }

    fun parameters(request: SearchAdsRequest): Array<Any> {
        return Predicates.parameters(
            request.userId,
            request.status.map { it.ordinal },
            request.type.map { it.ordinal },
            request.startDateFrom,
            request.startDateTo,
            request.endDateFrom,
            request.endDateTo
        )
    }

    private fun select() = "SELECT *"

    private fun from() = "FROM T_ADS"

    private fun where(request: SearchAdsRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.eq("user_fk", request.userId))
        predicates.add(Predicates.`in`("status", request.status))
        predicates.add(Predicates.`in`("type", request.type))
        predicates.add(Predicates.between("start_date", request.startDateFrom, request.startDateTo))
        predicates.add(Predicates.between("end_date", request.endDateFrom, request.endDateTo))
        return Predicates.where(predicates)
    }

    private fun limit(request: SearchAdsRequest) = "LIMIT ${request.limit}"

    private fun offset(request: SearchAdsRequest) = "OFFSET ${request.offset}"

    private fun order(request: SearchAdsRequest): String {
        val order = if (request.sortOrder == SortOrder.DESCENDING) "DESC" else "ASC"
        return when (request.sortBy) {
            AdsSortStrategy.CREATED -> "ORDER BY creation_date_time $order"
            AdsSortStrategy.START_DATE -> "ORDER BY start_date $order"
            AdsSortStrategy.TITLE -> "ORDER BY title $order"
            else -> ""
        }
    }
}
