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

    private fun select() = "SELECT *"

    private fun from(request: SearchReaderRequest) = if (request.subscribersOnly) {
        "FROM T_READER R JOIN T_SUBSCRIPTION S JOIN R.user_id=S.subscriber_fk"
    } else {
        "FROM T_READER R"
    }

    private fun where(request: SearchReaderRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.eq("R.user_id", request.userId))
        predicates.add(Predicates.eq("R.story_id", request.storyId))
        return Predicates.where(predicates)
    }

    private fun limit(request: SearchReaderRequest) = "LIMIT ${request.limit}"

    private fun offset(request: SearchReaderRequest) = "OFFSET ${request.offset}"

    private fun order() = "ORDER BY id DESC"
}
