package com.wutsi.blog.story.dao

import com.wutsi.blog.story.dto.SearchReaderRequest
import com.wutsi.blog.util.Predicates

class SearchReaderQueryBuilder {
    fun query(request: SearchReaderRequest): String {
        val select = select()
        val from = from(request)
        val where = where(request)
        val limit = limit(request)
        val offset = offset(request)
        val order = order()

        return "$select $from $where $order $limit $offset"
    }

    fun parameters(request: SearchReaderRequest): Array<Any> {
        return Predicates.parameters(
            request.storyId,
            request.subscribedToUserId,
        )
    }

    private fun select() = "SELECT R.*"

    private fun from(request: SearchReaderRequest) = if (request.subscribedToUserId != null) {
        "FROM T_READER R JOIN T_SUBSCRIPTION S ON R.user_id=S.subscriber_fk"
    } else {
        "FROM T_READER R"
    }

    private fun where(request: SearchReaderRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.eq("R.story_id", request.storyId))
        predicates.add(Predicates.eq("S.user_fk", request.subscribedToUserId))
        return Predicates.where(predicates)
    }

    private fun limit(request: SearchReaderRequest) = "LIMIT ${request.limit}"

    private fun offset(request: SearchReaderRequest) = "OFFSET ${request.offset}"

    private fun order() = "ORDER BY R.id DESC"
}
