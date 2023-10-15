package com.wutsi.blog.story.dao

import com.wutsi.blog.story.dto.SearchReaderRequest
import com.wutsi.blog.util.Predicates

class SearchReaderQueryBuilder {
    fun query(request: SearchReaderRequest): String {
        val select = select()
        val from = from()
        val where = where(request)
        val limit = limit(request)
        val offset = offset(request)
        val order = order()

        return "$select $from $where $order $limit $offset"
    }

    fun parameters(request: SearchReaderRequest): Array<Any> {
        return Predicates.parameters(
            request.userId,
            request.storyId,
        )
    }

    private fun select() = "SELECT DISTINCT *"

    private fun from() = "FROM T_READER"

    private fun where(request: SearchReaderRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.eq("user_id", request.userId))
        predicates.add(Predicates.eq("story_id", request.storyId))
        return Predicates.where(predicates)
    }

    private fun limit(request: SearchReaderRequest) = "LIMIT ${request.limit}"

    private fun offset(request: SearchReaderRequest) = "OFFSET ${request.offset}"

    private fun order() = "ORDER BY id DESC"
}
