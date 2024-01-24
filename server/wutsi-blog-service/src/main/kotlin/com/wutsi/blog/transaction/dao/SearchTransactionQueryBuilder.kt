package com.wutsi.blog.transaction.dao

import com.wutsi.blog.transaction.dto.SearchTransactionRequest
import com.wutsi.blog.util.Predicates

class SearchTransactionQueryBuilder {
    fun query(request: SearchTransactionRequest): String {
        val select = select()
        val from = from()
        val where = where(request)
        val order = order()
        val limit = limit(request)
        val offset = offset(request)

        return "$select $from $where $order $limit $offset"
    }

    fun parameters(request: SearchTransactionRequest): Array<Any> {
        return Predicates.parameters(
            request.walletId,
            request.storeId,
            request.userId,
            request.email,
            request.productIds,
            request.statuses.map { it.ordinal },
            request.types.map { it.ordinal },
            request.creationDateTimeFrom,
            request.creationDateTimeTo,
        )
    }

    private fun select() = "SELECT DISTINCT T.*"

    private fun from() = "FROM T_TRANSACTION T"

    private fun where(request: SearchTransactionRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.eq("T.wallet_fk", request.walletId))
        predicates.add(Predicates.eq("T.store_fk", request.storeId))
        predicates.add(Predicates.eq("T.user_fk", request.userId))
        predicates.add(Predicates.eq("T.email", request.email))
        predicates.add(Predicates.`in`("T.product_fk", request.productIds))
        predicates.add(Predicates.`in`("T.status", request.statuses.map { it.ordinal }))
        predicates.add(Predicates.`in`("T.type", request.types.map { it.ordinal }))
        predicates.add(
            Predicates.between(
                "T.creation_date_time",
                request.creationDateTimeFrom,
                request.creationDateTimeTo,
            ),
        )
        return Predicates.where(predicates)
    }

    private fun order() = "ORDER BY T.creation_date_time DESC"

    private fun limit(request: SearchTransactionRequest) = "LIMIT ${request.limit}"

    private fun offset(request: SearchTransactionRequest) = "OFFSET ${request.offset}"
}
