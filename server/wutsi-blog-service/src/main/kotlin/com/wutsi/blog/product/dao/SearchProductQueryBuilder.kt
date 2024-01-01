package com.wutsi.blog.product.dao

import com.wutsi.blog.SortOrder
import com.wutsi.blog.product.dto.ProductSortStrategy
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.util.Predicates

class SearchProductQueryBuilder {
    fun query(request: SearchProductRequest): String {
        val select = select()
        val from = from()
        val where = where(request)
        val order = order(request)
        val limit = limit(request)
        val offset = offset(request)

        return "$select $from $where $order $limit $offset"
    }

    fun parameters(request: SearchProductRequest): Array<Any> {
        return Predicates.parameters(
            request.productIds,
            request.excludeProductIds,
            request.externalIds,
            request.status?.ordinal,
            request.storeIds,
            request.available,
        )
    }

    private fun select() = "SELECT P.*"

    private fun from() = "FROM T_PRODUCT P"

    private fun where(request: SearchProductRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.`in`("P.id", request.productIds))
        predicates.add(Predicates.notIn("P.id", request.excludeProductIds))
        predicates.add(Predicates.`in`("P.external_id", request.externalIds))
        predicates.add(Predicates.eq("P.status", request.status))
        predicates.add(Predicates.`in`("P.store_fk", request.storeIds))
        predicates.add(Predicates.eq("P.available", request.available))

        return Predicates.where(predicates)
    }

    private fun order(request: SearchProductRequest): String {
        if (request.sortBy == ProductSortStrategy.NONE) {
            return ""
        }

        val order = if (request.sortOrder == SortOrder.DESCENDING) "DESC" else "ASC"
        return when (request.sortBy) {
            ProductSortStrategy.PUBLISHED -> "ORDER BY P.published_date_time $order"
            ProductSortStrategy.PRICE -> "ORDER BY P.price $order"
            ProductSortStrategy.ORDER_COUNT -> "ORDER BY P.order_count $order"
            ProductSortStrategy.TITLE -> "ORDER BY P.title $order"
            else -> ""
        }
    }

    private fun limit(request: SearchProductRequest) = "LIMIT ${request.limit}"

    private fun offset(request: SearchProductRequest) = "OFFSET ${request.offset}"
}
