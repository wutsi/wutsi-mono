package com.wutsi.blog.app.page.admin

import com.wutsi.blog.app.model.BarChartModel
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/me/stats")
class StatsController(
    kpiService: KpiService,
    storyService: StoryService,
    readerService: ReaderService,
    requestContext: RequestContext,
) : AbstractStatsController(kpiService, storyService, readerService, requestContext) {
    override fun pageName() = PageName.STATS

    override fun searchStoryReads(period: String?): List<KpiModel> =
        kpiService.search(
            SearchStoryKpiRequest(
                types = listOf(KpiType.READ),
                dimension = Dimension.SOURCE,
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

    override fun searchClicks(period: String?): List<KpiModel> =
        kpiService.search(
            SearchUserKpiRequest(
                types = listOf(KpiType.CLICK),
                dimension = Dimension.ALL,
                fromDate = fromDate(period),
            ),
        )

    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("page", createPage(title = "Statistics", description = ""))
        return "admin/stats"
    }

    @GetMapping("/chart/source")
    @ResponseBody
    override fun source(@RequestParam(required = false) period: String?): BarChartModel =
        kpiService.toBarChartModelByTrafficSource(
            kpis = searchStoryReads(period),
            type = KpiType.READ,
        )
}
