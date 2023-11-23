package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.comment.domain.CommentEntity
import com.wutsi.blog.comment.service.CommentService
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.kpi.service.KpiImporter
import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.util.DateUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class CommentKpiImporter(
    private val persister: KpiPersister,
    private val commentService: CommentService,
    private val storyService: StoryService,
) : KpiImporter {
    @Transactional
    override fun import(date: LocalDate): Long {
        val comments = getComments(date)
        val group = comments.groupBy { it.storyId }

        // Update story KPIS
        group.forEach { item ->
            persister.persistStory(date, KpiType.COMMENT, item.key, item.value.size.toLong())
        }

        // Update user KPIs
        val userIds = storyService.searchStories(
            SearchStoryRequest(
                storyIds = group.keys.toList(),
                limit = group.keys.size
            )
        ).map { it.userId }.toSet()
        userIds.forEach { userId ->
            persister.persistUser(date, KpiType.COMMENT, userId, TrafficSource.ALL)
        }

        return comments.size.toLong()
    }

    private fun getComments(date: LocalDate): List<CommentEntity> {
        val start = DateUtils.toDate(LocalDate.of(date.year, date.month, 1))
        val end = DateUtils.addDays(DateUtils.addMonths(start, 1), -1)
        return commentService.searchByDates(start, end)
    }
}
