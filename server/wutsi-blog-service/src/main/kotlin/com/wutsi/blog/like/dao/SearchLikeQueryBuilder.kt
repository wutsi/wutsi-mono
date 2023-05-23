package com.wutsi.blog.like.dao

import com.wutsi.blog.client.like.SearchLikeRequest
import com.wutsi.blog.util.Predicates

@Deprecated("")
class SearchLikeQueryBuilder {
    fun query(request: SearchLikeRequest): String {
        val select = select()
        val from = from(request)
        val where = where(request)
        val limit = limit(request)
        val offset = offset(request)

        return "$select $from $where $limit $offset"
    }

    fun count(request: SearchLikeRequest): String {
        val from = from(request)
        val where = where(request)

        return "SELECT story_fk, count(*) $from $where GROUP BY story_fk"
    }

    fun parameters(request: SearchLikeRequest): Array<Any> {
        return Predicates.parameters(
            request.storyIds,
            request.since,
            request.authorId,
            request.userId,
            request.deviceId,
        )
    }

    private fun select() = "SELECT L.*"

    private fun from(request: SearchLikeRequest): String {
        return if (request.authorId == null) {
            "FROM T_LIKE L"
        } else {
            "FROM T_LIKE L JOIN T_STORY S ON L.story_fk=S.id"
        }
    }

    private fun where(request: SearchLikeRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.`in`("L.story_fk", request.storyIds))
        predicates.add(Predicates.gte("L.like_date_time", request.since))
        predicates.add(Predicates.eq("S.user_fk", request.authorId))
        predicates.add(
            Predicates.or(
                Predicates.eq("L.user_fk", request.userId),
                Predicates.eq("L.device_id", request.deviceId),
            ),
        )

        return Predicates.where(predicates)
    }

    private fun limit(request: SearchLikeRequest) = "LIMIT ${request.limit}"

    private fun offset(request: SearchLikeRequest) = "OFFSET ${request.offset}"
}
