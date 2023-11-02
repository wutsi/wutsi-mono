package com.wutsi.blog.app.page.admin

import com.wutsi.blog.app.model.KpiModel
import com.wutsi.blog.app.model.ReaderModel
import com.wutsi.blog.app.service.KpiService
import com.wutsi.blog.app.service.ReaderService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.kpi.dto.Dimension
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import com.wutsi.blog.story.dto.SearchReaderRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/me/stats")
class StatsController(
    service: KpiService,
    storyService: StoryService,
    readerService: ReaderService,
    requestContext: RequestContext,
) : AbstractStatsController(service, storyService, readerService, requestContext) {
    override fun pageName() = PageName.STATS

    override fun searchStoryReads(period: String?): List<KpiModel> =
        kpiService.search(
            SearchStoryKpiRequest(
                types = listOf(KpiType.READ),
                dimension = Dimension.ALL,
                fromDate = fromDate(period),
            ),
        )

    override fun searchReads(period: String?): List<KpiModel> =
        kpiService.search(
            SearchUserKpiRequest(
                types = listOf(KpiType.READ),
                dimension = Dimension.ALL,
                fromDate = fromDate(period),
            ),
        )

    override fun searchReadTime(period: String?): List<KpiModel> =
        kpiService.search(
            SearchUserKpiRequest(
                types = listOf(KpiType.DURATION),
                dimension = Dimension.ALL,
                fromDate = fromDate(period),
            ),
        )

    override fun searchSubscriptions(period: String?): List<KpiModel> =
        kpiService.search(
            SearchUserKpiRequest(
                types = listOf(KpiType.SUBSCRIPTION),
                dimension = Dimension.ALL,
                fromDate = fromDate(period),
            ),
        )

    override fun searchSources(period: String?): List<KpiModel> =
        kpiService.search(
            SearchUserKpiRequest(
                types = listOf(KpiType.READ),
                dimension = Dimension.SOURCE,
                fromDate = fromDate(period),
            ),
        )

    override fun searchReaders(): List<ReaderModel> =
        readerService.search(
            SearchReaderRequest(
                limit = 50,
            ),
        )

    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("page", createPage(title = "Statistics", description = ""))
        return "admin/stats"
    }

//    @GetMapping("/chart/stories")
//    fun stories(
//        @RequestParam(required = false) period: String? = null,
//        model: Model,
//    ): String {
//        val kpis = service.search(
//            SearchStoryKpiRequest(
//                types = listOf(KpiType.READ),
//                userId = requestContext.currentUser()?.id,
//                fromDate = fromDate(period),
//            ),
//        )
//
//        if (kpis.isNotEmpty()) {
//            // Select the top 10 stories with highest read
//            val storyIds = kpis.map { it.targetId }.toSet()
//            val storyIdCountMap = storyIds.map {
//                StoryModel(
//                    id = it,
//                    readCount = computeReadCount(it, kpis)
//                )
//            }.sortedByDescending { it.readCount }
//                .take(10)
//                .associateBy { it.id }
//
//            // Get story details
//            val stories = storyService.search(
//                request = SearchStoryRequest(
//                    storyIds = storyIdCountMap.keys.toList(),
//                    limit = storyIdCountMap.size,
//                    sortBy = StorySortStrategy.NONE,
//                ),
//            ).map {
//                it.copy(
//                    readCount = storyIdCountMap[it.id]?.readCount ?: 0
//                )
//            }.sortedByDescending { it.readCount }
//
//            if (stories.isNotEmpty()) {
//                model.addAttribute("stories", stories)
//            }
//        }
//
//        return "admin/fragment/stats-stories"
//    }
//
//    @GetMapping("/chart/read")
//    @ResponseBody
//    fun read(
//        @RequestParam(required = false) period: String? = null,
//    ): BarChartModel =
//        service.toBarChartModel(
//            kpis = searchReads(period),
//            type = KpiType.READ,
//        )
//
//    @GetMapping("/chart/read-time")
//    @ResponseBody
//    fun readTime(
//        @RequestParam(required = false) period: String? = null,
//    ): BarChartModel =
//        service.toBarChartModel(
//            kpis = service.search(
//                SearchUserKpiRequest(
//                    types = listOf(KpiType.DURATION),
//                    dimension = Dimension.ALL,
//                    fromDate = fromDate(period),
//                ),
//            ),
//            type = KpiType.DURATION,
//        )
//
//    @GetMapping("/chart/subscription")
//    @ResponseBody
//    fun subscription(
//        @RequestParam(required = false) period: String? = null,
//    ): BarChartModel =
//        service.toBarChartModel(
//            kpis = service.search(
//                SearchUserKpiRequest(
//                    types = listOf(KpiType.SUBSCRIPTION),
//                    dimension = Dimension.ALL,
//                    fromDate = fromDate(period),
//                ),
//            ),
//            type = KpiType.SUBSCRIPTION,
//        )
//
//    @GetMapping("/chart/source")
//    @ResponseBody
//    fun source(
//        @RequestParam(required = false) period: String? = null,
//    ): BarChartModel =
//        service.toBarChartModelByTrafficSource(
//            kpis = service.search(
//                SearchUserKpiRequest(
//                    types = listOf(KpiType.READ),
//                    dimension = Dimension.SOURCE,
//                    fromDate = fromDate(period),
//                ),
//            ),
//            type = KpiType.READ,
//        )
}
