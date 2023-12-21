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

    override fun searchStoryKpis(period: String?, types: List<KpiType>): List<KpiModel> =
        kpiService.search(
            SearchStoryKpiRequest(
                types = types,
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

    override fun searchReaders(limit: Int, offset: Int): List<ReaderModel> =
        readerService.search(
            SearchReaderRequest(
                limit = limit,
                offset = offset,
            ),
        )

    override fun searchClicks(period: String?): List<KpiModel> =
        emptyList()

    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("page", createPage(title = "Statistics", description = ""))
        return "admin/stats-2"
    }

    @GetMapping("/chart/user")
    @ResponseBody
    fun user(@RequestParam(required = false) period: String? = null): BarChartModel =
        kpiService.toBarChartModel(
            kpis = kpiService.search(
                SearchUserKpiRequest(
                    types = listOf(KpiType.USER),
                    dimension = Dimension.ALL,
                    fromDate = fromDate(period),
                ),
            ),
            type = KpiType.USER,
        )

    @GetMapping("/chart/blog")
    @ResponseBody
    fun blog(@RequestParam(required = false) period: String? = null): BarChartModel =
        kpiService.toBarChartModel(
            kpis = kpiService.search(
                SearchUserKpiRequest(
                    types = listOf(KpiType.USER_BLOG),
                    dimension = Dimension.ALL,
                    fromDate = fromDate(period),
                ),
            ),
            type = KpiType.USER_BLOG,
        )

    @GetMapping("/chart/wpp")
    @ResponseBody
    fun wpp(@RequestParam(required = false) period: String? = null): BarChartModel =
        kpiService.toBarChartModel(
            kpis = kpiService.search(
                SearchUserKpiRequest(
                    types = listOf(KpiType.USER_WPP),
                    dimension = Dimension.ALL,
                    fromDate = fromDate(period),
                ),
            ),
            type = KpiType.USER_WPP,
        )

    @GetMapping("/chart/store")
    @ResponseBody
    fun store(@RequestParam(required = false) period: String? = null): BarChartModel =
        kpiService.toBarChartModel(
            kpis = kpiService.search(
                SearchUserKpiRequest(
                    types = listOf(KpiType.STORE),
                    dimension = Dimension.ALL,
                    fromDate = fromDate(period),
                ),
            ),
            type = KpiType.STORE,
        )

    @GetMapping("/chart/publication")
    @ResponseBody
    fun publication(@RequestParam(required = false) period: String? = null): BarChartModel =
        kpiService.toBarChartModel(
            kpis = kpiService.search(
                SearchUserKpiRequest(
                    types = listOf(KpiType.PUBLICATION),
                    dimension = Dimension.ALL,
                    fromDate = fromDate(period),
                ),
            ),
            type = KpiType.READER,
        )

    @GetMapping("/chart/product")
    @ResponseBody
    fun product(@RequestParam(required = false) period: String? = null): BarChartModel =
        kpiService.toBarChartModel(
            kpis = kpiService.search(
                SearchUserKpiRequest(
                    types = listOf(KpiType.PRODUCT),
                    dimension = Dimension.ALL,
                    fromDate = fromDate(period),
                ),
            ),
            type = KpiType.READER,
        )

    @GetMapping("/chart/donation")
    @ResponseBody
    fun donation(@RequestParam(required = false) period: String? = null): BarChartModel =
        kpiService.toBarChartModel(
            kpis = kpiService.search(
                SearchUserKpiRequest(
                    types = listOf(KpiType.DONATION),
                    dimension = Dimension.ALL,
                    fromDate = fromDate(period),
                ),
            ),
            type = KpiType.READER,
        )

    @GetMapping("/chart/donation-value")
    @ResponseBody
    fun donationValue(@RequestParam(required = false) period: String? = null): BarChartModel =
        kpiService.toBarChartModel(
            kpis = kpiService.search(
                SearchUserKpiRequest(
                    types = listOf(KpiType.DONATION_VALUE),
                    dimension = Dimension.ALL,
                    fromDate = fromDate(period),
                ),
            ),
            type = KpiType.READER,
        )

    @GetMapping("/chart/sales")
    @ResponseBody
    fun sales(@RequestParam(required = false) period: String? = null): BarChartModel =
        kpiService.toBarChartModel(
            kpis = kpiService.search(
                SearchUserKpiRequest(
                    types = listOf(KpiType.SALES),
                    dimension = Dimension.ALL,
                    fromDate = fromDate(period),
                ),
            ),
            type = KpiType.SALES,
        )

    @GetMapping("/chart/sales-value")
    @ResponseBody
    fun salesValue(@RequestParam(required = false) period: String? = null): BarChartModel =
        kpiService.toBarChartModel(
            kpis = kpiService.search(
                SearchUserKpiRequest(
                    types = listOf(KpiType.SALES_VALUE),
                    dimension = Dimension.ALL,
                    fromDate = fromDate(period),
                ),
            ),
            type = KpiType.SALES,
        )
}
