package com.wutsi.blog.comment.dao

import com.wutsi.blog.client.comment.SearchCommentRequest
import com.wutsi.blog.util.Predicates

@Deprecated("")
class SearchCommentQueryBuilder {
    fun query(request: SearchCommentRequest): String {
        val select = select()
        val from = from(request)
        val where = where(request)
        val limit = limit(request)
        val offset = offset(request)

        return "$select $from $where $limit $offset"
    }

    fun count(request: SearchCommentRequest): String {
        val from = from(request)
        val where = where(request)

        return "SELECT story_fk, count(*) $from $where GROUP BY story_fk"
    }

    fun parameters(request: SearchCommentRequest): Array<Any> {
        return Predicates.parameters(
            request.storyIds,
            request.since,
            request.authorId,
        )
    }

    private fun select() = "SELECT C.*"

    private fun from(request: SearchCommentRequest): String {
        return if (request.authorId == null) {
            "FROM T_COMMENT C"
        } else {
            "FROM T_COMMENT C JOIN T_STORY S ON C.story_fk=S.id"
        }
    }

    private fun where(request: SearchCommentRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.`in`("C.story_fk", request.storyIds))
        predicates.add(Predicates.gte("C.creation_date_time", request.since))
        predicates.add(Predicates.eq("S.user_fk", request.authorId))

        return Predicates.where(predicates)
    }

    private fun limit(request: SearchCommentRequest) = "LIMIT ${request.limit}"

    private fun offset(request: SearchCommentRequest) = "OFFSET ${request.offset}"
}
