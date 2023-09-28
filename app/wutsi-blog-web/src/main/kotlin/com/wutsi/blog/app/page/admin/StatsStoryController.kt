package com.wutsi.blog.app.page.admin

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.model.BarChartModel
import com.wutsi.blog.app.service.KpiService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.kpi.dto.Dimension
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class StatsStoryController(
    private val service: KpiService,
    private val storyService: StoryService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.STATS_STORY

    @GetMapping("/me/stats/story")
    fun index(
        @RequestParam(name = "story-id") id: Long,
        model: Model,
    ): String {
        val story = storyService.get(id, withKpis = true)

        model.addAttribute("story", story)
        model.addAttribute("page", createPage(title = "Statistics", description = ""))
        return "admin/stats-story"
    }

    @GetMapping("/me/stats/story/chart/read")
    @ResponseBody
    fun chart(
        @RequestParam(name = "story-id") storyId: Long,
    ): BarChartModel =
        service.toBarChartModel(
            kpis = service.search(
                SearchStoryKpiRequest(
                    storyIds = listOf(storyId),
                    types = listOf(KpiType.READ),
                ),
            ),
            type = KpiType.READ,
        )

    @GetMapping("/me/stats/story/chart/source")
    @ResponseBody
    fun source(
        @RequestParam(name = "story-id") storyId: Long,
    ): BarChartModel =
        service.toKpiModelBySource(
            kpis = service.search(
                SearchStoryKpiRequest(
                    storyIds = listOf(storyId),
                    types = listOf(KpiType.READ),
                    dimension = Dimension.SOURCE,
                ),
            ),
            type = KpiType.READ,
        )
}
