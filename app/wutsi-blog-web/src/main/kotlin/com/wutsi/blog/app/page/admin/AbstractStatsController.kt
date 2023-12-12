package com.wutsi.blog.app.page.admin

import com.wutsi.blog.app.model.BarChartModel
import com.wutsi.blog.app.model.KpiModel
import com.wutsi.blog.app.model.ReaderModel
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.page.AbstractPageController
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
    protected abstract fun searchStoryKpis(period: String?, types: List<KpiType>): List<KpiModel>

    protected abstract fun searchReads(period: String?): List<KpiModel>

    protected abstract fun searchReadTime(period: String?): List<KpiModel>

    protected abstract fun searchSubscriptions(period: String?): List<KpiModel>

    protected abstract fun searchSources(period: String?): List<KpiModel>

    protected abstract fun searchClicks(period: String?): List<KpiModel>

    protected abstract fun searchReaders(limit: Int = 50, offset: Int = 0): List<ReaderModel>

    protected open fun maxStories(): Int = 10

    @GetMapping("/stories")
    fun stories(@RequestParam(required = false) period: String? = null, model: Model): String {
        val kpis = searchStoryKpis(
            period = period,
            types = listOf(
                KpiType.READ,
                KpiType.READER,
                KpiType.CLICK,
                KpiType.READER_EMAIL,
                KpiType.DURATION
            )
        )

        if (kpis.isNotEmpty()) {
            // Select the top 10 stories with highest read
            val storyIds = kpis.map { it.targetId }.toSet()
            val storyIdCountMap = storyIds.map {
                StoryModel(
                    id = it,
                    readCount = sum(it, kpis, KpiType.READ),
                    totalDurationSeconds = sum(it, kpis, KpiType.DURATION),
                    clickCount = avg(it, kpis, KpiType.CLICK),
                    readerCount = avg(it, kpis, KpiType.READER),
                    emailReaderCount = avg(it, kpis, KpiType.READER_EMAIL),
                )
            }.sortedByDescending { it.totalDurationSeconds }
                .take(maxStories())
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
                if (period == null) {
                    it
                } else {
                    it.copy(
                        readCount = storyIdCountMap[it.id]?.readCount ?: 0,
                        totalDurationSeconds = storyIdCountMap[it.id]?.totalDurationSeconds ?: 0,
                        clickCount = storyIdCountMap[it.id]?.clickCount ?: 0,
                        readerCount = storyIdCountMap[it.id]?.readerCount ?: 0,
                        emailReaderCount = storyIdCountMap[it.id]?.emailReaderCount ?: 0,
                    )
                }
            }.sortedByDescending { it.totalDurationSeconds }

            if (stories.isNotEmpty()) {
                model.addAttribute("stories", stories)
            }
        }

        return "admin/fragment/stats-stories"
    }

    @GetMapping("/readers")
    fun readers(
        @RequestParam(required = false) limit: Int? = null,
        @RequestParam(required = false) offset: Int? = null,
        model: Model
    ): String {
        val readers = searchReaders(limit ?: 50, offset ?: 0)
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

    @GetMapping("/source")
    open fun source(
        @RequestParam(required = false) period: String? = null,
        model: Model,
    ): String {
        val kpis = searchSources(period)
        val total = kpis.sumOf { it.value }
        if (total != 0.0) {
            val xkpis = searchSources(period)
                .groupBy { it.source }
                .map { entry ->
                    KpiModel(
                        id = 0,
                        targetId = entry.value[0].targetId,
                        type = entry.value[0].type,
                        date = entry.value[0].date,
                        value = 100 * entry.value.sumOf { it.value } / total,
                        source = entry.value[0].source,
                    )
                }
                .sortedByDescending { it.value }

            model.addAttribute("kpis", xkpis)
        }
        return "admin/fragment/stats-source"
    }

    @GetMapping("/chart/click")
    @ResponseBody
    fun click(@RequestParam(required = false) period: String? = null): BarChartModel =
        kpiService.toBarChartModel(
            kpis = searchClicks(period).map { it.copy(value = it.value / 10000) },
            type = KpiType.CLICK_RATE,
        )

    protected fun sum(targetId: Long, kpis: List<KpiModel>, type: KpiType): Long {
        val filtered = kpis.filter { it.targetId == targetId && it.type == type }
        return if (filtered.isEmpty()) {
            0L
        } else {
            filtered.reduce { acc, kpi -> acc.copy(value = acc.value + kpi.value) }
                .value
                .toLong()
        }
    }

    protected fun avg(targetId: Long, kpis: List<KpiModel>, type: KpiType): Long {
        val count = kpis.filter { it.targetId == targetId && it.type == type }.size
        return if (count == 0) {
            0L
        } else {
            sum(targetId, kpis, type) / count
        }
    }

    protected fun fromDate(period: String?): LocalDate? =
        when (period?.lowercase()) {
            "l180" -> LocalDate.now().minusDays(180)
            "l90" -> LocalDate.now().minusDays(90)
            "l30" -> LocalDate.now().minusDays(30)
            else -> null
        }
}
