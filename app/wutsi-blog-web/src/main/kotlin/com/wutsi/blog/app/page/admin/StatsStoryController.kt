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
import com.wutsi.blog.story.dto.SearchReaderRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/me/stats/story")
class StatsStoryController(
    kpiService: KpiService,
    storyService: StoryService,
    readerService: ReaderService,
    requestContext: RequestContext,
) : AbstractStatsController(kpiService, storyService, readerService, requestContext) {
    override fun pageName() = PageName.STATS_STORY

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
            SearchStoryKpiRequest(
                storyIds = listOf(getStoryId()),
                types = listOf(KpiType.READ),
                fromDate = fromDate(period),
            ),
        )

    override fun searchReadTime(period: String?): List<KpiModel> =
        kpiService.search(
            SearchStoryKpiRequest(
                storyIds = listOf(getStoryId()),
                types = listOf(KpiType.DURATION),
                fromDate = fromDate(period),
            ),
        )

    override fun searchClicks(period: String?): List<KpiModel> =
        kpiService.search(
            SearchStoryKpiRequest(
                storyIds = listOf(getStoryId()),
                types = listOf(KpiType.CLICK_RATE),
                fromDate = fromDate(period),
            ),
        )

    override fun searchSubscriptions(period: String?): List<KpiModel> =
        emptyList()

    override fun searchSources(period: String?): List<KpiModel> =
        kpiService.search(
            SearchStoryKpiRequest(
                storyIds = listOf(getStoryId()),
                types = listOf(KpiType.READ),
                dimension = Dimension.SOURCE,
                fromDate = fromDate(period),
            ),
        )

    override fun searchReaders(limit: Int, offset: Int): List<ReaderModel> =
        readerService.search(
            SearchReaderRequest(
                storyId = getStoryId(),
                subscribedToUserId = requestContext.currentUser()!!.id,
                limit = limit,
                offset = offset,
            ),
        )

    private fun getStoryId(): Long =
        requestContext.request.getParameter("story-id").toLong()

    @GetMapping
    fun index(@RequestParam(name = "story-id") id: Long, model: Model): String {
        val story = storyService.get(id)

        model.addAttribute("story", story)
        model.addAttribute("page", createPage(title = "Statistics", description = ""))
        model.addAttribute("monetization", story.user.wpp)
        return "admin/stats-story"
    }

    @GetMapping("/chart/wpp")
    @ResponseBody
    fun user(@RequestParam(required = false) period: String? = null): BarChartModel =
        kpiService.toBarChartModel(
            kpis = kpiService.search(
                SearchStoryKpiRequest(
                    storyIds = listOf(getStoryId()),
                    types = listOf(KpiType.WPP_EARNING, KpiType.WPP_BONUS),
                    dimension = Dimension.ALL,
                    fromDate = fromDate(period),
                ),
            ),
        )
}
