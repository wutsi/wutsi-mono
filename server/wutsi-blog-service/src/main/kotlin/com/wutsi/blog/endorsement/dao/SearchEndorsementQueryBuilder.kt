package com.wutsi.blog.endorsement.dao

import com.wutsi.blog.endorsement.dto.SearchEndorsementRequest
import com.wutsi.blog.util.Predicates

class SearchEndorsementQueryBuilder {
    fun query(request: SearchEndorsementRequest): String {
        val select = select()
        val from = from()
        val where = where(request)
        val limit = limit(request)
        val offset = offset(request)

        return "$select $from $where $limit $offset"
    }

    fun count(request: SearchEndorsementRequest): String {
        val from = from()
        val where = where(request)

        return "SELECT count(*) $from $where"
    }

    fun parameters(request: SearchEndorsementRequest): Array<Any> {
        return Predicates.parameters(
            request.userIds,
            request.endorserId,
        )
    }

    private fun select() = "SELECT *"

    private fun from() = "FROM T_ENDORSEMENT"

    private fun where(request: SearchEndorsementRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.`in`("user_fk", request.userIds))
        predicates.add(Predicates.eq("endorser_fk", request.endorserId))

        return Predicates.where(predicates)
    }

    private fun limit(request: SearchEndorsementRequest) = "LIMIT ${request.limit}"

    private fun offset(request: SearchEndorsementRequest) = "OFFSET ${request.offset}"
}
