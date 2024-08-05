package com.wutsi.blog.story.dao

import com.wutsi.blog.SortOrder
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.service.TagService
import com.wutsi.blog.util.Predicates

class SearchStoryQueryBuilder(
    private val tagService: TagService
) {
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

    fun parameters(request: SearchStoryRequest): Array<Any> = Predicates.parameters(
            request.userIds,
            request.excludeUserIds,
            request.status?.ordinal,
            request.publishedStartDate,
            request.publishedEndDate,
            request.topicId,
            request.storyIds,
            request.language,
            request.scheduledPublishedStartDate,
            request.scheduledPublishedEndDate,
            request.wpp,
            toTagNames(request.tags).ifEmpty { null },
            if (request.activeUserOnly) true else null,
            request.userCountries.map { it.lowercase() },
            request.categoryIds,
            request.categoryIds,

            // Last parameters
            false, // T_STORY.deleted
            false, // T_USER.suspended
        )

    private fun select() = "select DISTINCT S.*"

    private fun from(request: SearchStoryRequest): String {
        var query = "from T_STORY S join T_USER U on S.user_fk=U.id"
        if (request.categoryIds.isNotEmpty()) {
            query = "$query join T_CATEGORY C on S.category_fk=C.id"
        }
        return if (request.tags.isEmpty()) {
            query
        } else {
            "$query JOIN T_STORY_TAG ST ON ST.story_fk=S.id JOIN T_TAG T ON ST.tag_fk=T.id"
        }
    }

    private fun where(request: SearchStoryRequest): String {
        val predicates = mutableListOf<String?>()
        predicates.add(Predicates.`in`("S.user_fk", request.userIds))
        predicates.add(Predicates.notIn("S.user_fk", request.excludeUserIds))
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
        predicates.add(Predicates.eq("U.wpp", request.wpp))
        if (request.tags.isNotEmpty()) {
            predicates.add(
                Predicates.`in`(
                    "T.name",
                    toTagNames(request.tags),
                ),
            )
        }
        if (request.activeUserOnly) {
            predicates.add(Predicates.eq("U.active", true))
        }
        predicates.add(Predicates.`in`("U.country", request.userCountries))

        predicates.add(
            Predicates.or(
                Predicates.`in`("S.category_fk", request.categoryIds),
                Predicates.`in`("C.parent_fk", request.categoryIds)
            )
        )

        // Last predicates
        predicates.add(Predicates.eq("S.deleted", false))
        predicates.add(Predicates.eq("U.suspended", false))
        return Predicates.where(predicates)
    }

    private fun toTagNames(tags: List<String>): List<String> =
        tags
            .map { tagService.toName(it) }
            .toSet() // ensure unique
            .toList()

    private fun order(request: SearchStoryRequest): String {
        if (request.sortBy == StorySortStrategy.NONE) {
            return ""
        }

        val order = if (request.sortOrder == SortOrder.DESCENDING) "DESC" else "ASC"
        return when (request.sortBy) {
            StorySortStrategy.MODIFIED -> "ORDER BY modification_date_time $order"
            StorySortStrategy.PUBLISHED -> "ORDER BY published_date_time $order"
            StorySortStrategy.RECOMMENDED -> "ORDER BY published_date_time DESC"
            StorySortStrategy.CREATED -> "ORDER BY id $order"
            StorySortStrategy.POPULARITY -> "ORDER BY read_count DESC"
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
