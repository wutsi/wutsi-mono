package com.wutsi.blog.app.page.admin

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.model.BarChartModel
import com.wutsi.blog.app.model.KpiModel
import com.wutsi.blog.app.model.ReaderModel
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.KpiService
import com.wutsi.blog.app.service.ReaderService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.time.LocalDate

abstract class AbstractStatsController(
    protected val kpiService: KpiService,
    protected val storyService: StoryService,
    protected val readerService: ReaderService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    protected abstract fun searchStoryReads(period: String?): List<KpiModel>

    protected abstract fun searchReads(period: String?): List<KpiModel>

    protected abstract fun searchReadTime(period: String?): List<KpiModel>

    protected abstract fun searchSubscriptions(period: String?): List<KpiModel>

    protected abstract fun searchSources(period: String?): List<KpiModel>

    protected abstract fun searchReaders(): List<ReaderModel>

    @GetMapping("/stories")
    fun stories(@RequestParam(required = false) period: String? = null, model: Model): String {
        val kpis = searchStoryReads(period = period)

        if (kpis.isNotEmpty()) {
            // Select the top 10 stories with highest read
            val storyIds = kpis.map { it.targetId }.toSet()
            val storyIdCountMap = storyIds.map {
                StoryModel(
                    id = it,
                    readCount = computeReadCount(it, kpis)
                )
            }.sortedByDescending { it.readCount }
                .take(10)
                .associateBy { it.id }

            // Get story details
            val tmp = storyService.search(
                request = SearchStoryRequest(
                    storyIds = storyIdCountMap.keys.toList(),
                    limit = storyIdCountMap.size,
                    sortBy = StorySortStrategy.NONE,
                ),
            )
            val stories = tmp.map {
                it.copy(
                    readCount = storyIdCountMap[it.id]?.readCount ?: 0
                )
            }.sortedByDescending { it.readCount }

            if (stories.isNotEmpty()) {
                model.addAttribute("stories", stories)
            }
        }

        return "admin/fragment/stats-stories"
    }

    @GetMapping("/readers")
    fun readers(model: Model): String {
        val readers = searchReaders()
        if (readers.isNotEmpty()) {
            model.addAttribute("readers", readers)
        }
        return "admin/fragment/stats-readers"
    }

    @GetMapping("/chart/read")
    @ResponseBody
    fun read(@RequestParam(required = false) period: String? = null): BarChartModel =
        kpiService.toBarChartModel(
            kpis = searchReads(period),
            type = KpiType.READ,
        )

    @GetMapping("/chart/read-time")
    @ResponseBody
    fun readTime(@RequestParam(required = false) period: String? = null): BarChartModel =
        kpiService.toBarChartModel(
            kpis = searchReadTime(period).map { it.copy(value = it.value / 3600) },
            type = KpiType.DURATION,
        )

    @GetMapping("/chart/subscription")
    @ResponseBody
    fun subscription(@RequestParam(required = false) period: String? = null): BarChartModel =
        kpiService.toBarChartModel(
            kpis = searchSubscriptions(period),
            type = KpiType.SUBSCRIPTION,
        )

    @GetMapping("/chart/source")
    @ResponseBody
    open fun source(
        @RequestParam(required = false) period: String? = null,
    ): BarChartModel =
        kpiService.toKpiModelBySource(
            kpis = searchSources(period),
            type = KpiType.READ,
        )

    private fun computeReadCount(storyId: Long, kpis: List<KpiModel>): Long {
        val filtered = kpis.filter { it.targetId == storyId }
        return filtered.reduce { acc, kpi -> acc.copy(value = acc.value + kpi.value) }
            .value
    }

    protected fun fromDate(period: String?): LocalDate? =
        when (period?.lowercase()) {
            "l180" -> LocalDate.now().minusDays(180)
            "l90" -> LocalDate.now().minusDays(90)
            "l30" -> LocalDate.now().minusDays(30)
            else -> null
        }
}
