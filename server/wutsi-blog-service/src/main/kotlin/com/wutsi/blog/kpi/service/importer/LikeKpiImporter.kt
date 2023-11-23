package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.kpi.service.KpiImporter
import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.like.domain.LikeEntity
import com.wutsi.blog.like.service.LikeService
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.util.DateUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class LikeKpiImporter(
    private val persister: KpiPersister,
    private val likeService: LikeService,
    private val storyService: StoryService,
) : KpiImporter {
    @Transactional
    override fun import(date: LocalDate): Long {
        val likes = getLikes(date)
        val group = likes.groupBy { it.storyId }

        // Update story KPIS
        group.forEach { item ->
            persister.persistStory(date, KpiType.LIKE, item.key, item.value.size.toLong())
        }

        // Update user KPIs
        val userIds = storyService.searchStories(
            SearchStoryRequest(
                storyIds = group.keys.toList(),
                limit = group.keys.size
            )
        ).map { it.userId }.toSet()
        userIds.forEach { userId ->
            persister.persistUser(date, KpiType.LIKE, userId, TrafficSource.ALL)
        }

        return likes.size.toLong()
    }

    private fun getLikes(date: LocalDate): List<LikeEntity> {
        val start = DateUtils.toDate(LocalDate.of(date.year, date.month, 1))
        val end = DateUtils.addDays(DateUtils.addMonths(start, 1), -1)
        return likeService.searchByDates(start, end)
    }
}
