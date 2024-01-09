package com.wutsi.blog.product.dao

import com.wutsi.blog.product.dto.SearchBookRequest
import com.wutsi.blog.util.Predicates

class SearchBookQueryBuilder {
    fun query(request: SearchBookRequest): String {
        val select = select()
        val from = from()
        val where = where(request)
        val limit = limit(request)
        val offset = offset(request)

        return "$select $from $where $limit $offset"
    }

    fun parameters(request: SearchBookRequest): Array<Any> {
        return Predicates.parameters(
            request.userId,
            request.bookIds,
            request.productIds,
        )
    }

    private fun select() = "SELECT *"

    private fun from() = "FROM T_BOOK"

    private fun where(request: SearchBookRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.eq("user_fk", request.userId))
        predicates.add(Predicates.`in`("id", request.bookIds))
        predicates.add(Predicates.`in`("product_fk", request.productIds))

        return Predicates.where(predicates)
    }

    private fun limit(request: SearchBookRequest) = "LIMIT ${request.limit}"

    private fun offset(request: SearchBookRequest) = "OFFSET ${request.offset}"
}
