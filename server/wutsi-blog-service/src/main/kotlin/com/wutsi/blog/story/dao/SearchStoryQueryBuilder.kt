package com.wutsi.blog.story.dao

import com.wutsi.blog.SortOrder
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.service.TagService
import com.wutsi.blog.util.Predicates

class SearchStoryQueryBuilder(private val tagService: TagService) {
    fun query(request: SearchStoryRequest): String {
        val select = select()
        val from = from(request)
        val where = where(request)
        val order = order(request)
        val limit = limit(request)
        val offset = offset(request)

        return "$select $from $where $order $limit $offset"
    }

    fun count(request: SearchStoryRequest): String {
        val from = from(request)
        val where = where(request)

        return "SELECT count(*) $from $where"
    }

    fun parameters(request: SearchStoryRequest): Array<Any> {
        return Predicates.parameters(
            request.userIds,
            request.status?.ordinal,
            request.publishedStartDate,
            request.publishedEndDate,
            request.topicId,
            request.storyIds,
            request.language,
            request.scheduledPublishedStartDate,
            request.scheduledPublishedEndDate,
            toTagNames(request.tags).ifEmpty { null },

            // Last parameter
            false, // T_STORY.deleted
            false, // T_USER.deleted
        )
    }

    private fun select() = "SELECT DISTINCT S.*"

    private fun from(request: SearchStoryRequest): String {
        val query = "FROM T_STORY S JOIN T_USER U ON S.user_fk=U.id"
        return if (request.tags.isEmpty()) {
            query
        } else {
            "$query JOIN T_STORY_TAG ST ON ST.story_fk=S.id JOIN T_TAG T ON ST.tag_fk=T.id"
        }
    }

    private fun where(request: SearchStoryRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.`in`("S.user_fk", request.userIds))
        predicates.add(Predicates.eq("S.status", request.status))
        predicates.add(
            Predicates.between(
                "S.published_date_time",
                request.publishedStartDate,
                request.publishedEndDate,
            ),
        )
        predicates.add(Predicates.eq("S.topic_fk", request.topicId))
        predicates.add(Predicates.`in`("S.id", request.storyIds))
        predicates.add(Predicates.eq("S.language", request.language))
        predicates.add(
            Predicates.between(
                "S.scheduled_publish_date_time",
                request.scheduledPublishedStartDate,
                request.scheduledPublishedEndDate,
            ),
        )

        if (request.tags.isNotEmpty()) {
            predicates.add(
                Predicates.`in`(
                    "T.name",
                    toTagNames(request.tags),
                ),
            )
        }

        // Last predicated
        predicates.add(Predicates.eq("S.deleted", false))
        predicates.add(Predicates.eq("U.suspended", false))
        return Predicates.where(predicates)
    }

    private fun toTagNames(tags: List<String>): List<String> =
        tags.map { tagService.toName(it) }
            .toSet() // ensure unique
            .toList()

    private fun order(request: SearchStoryRequest): String {
        val order = if (request.sortOrder == SortOrder.DESCENDING) "DESC" else "ASC"
        return when (request.sortBy) {
            StorySortStrategy.MODIFIED -> "ORDER BY modification_date_time $order"
            StorySortStrategy.PUBLISHED -> "ORDER BY published_date_time $order"
            StorySortStrategy.RECOMMENDED -> "ORDER BY published_date_time DESC"
            StorySortStrategy.CREATED -> "ORDER BY id $order"
            StorySortStrategy.POPULARITY -> "ORDER BY read_count $order"
            else -> ""
        }
    }

    private fun limit(request: SearchStoryRequest) =
        if (request.dedupUser) {
            "LIMIT " + (4 * request.limit)
        } else {
            "LIMIT ${request.limit}"
        }

    private fun offset(request: SearchStoryRequest) = "OFFSET ${request.offset}"
}
