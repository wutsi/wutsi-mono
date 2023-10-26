package com.wutsi.blog.app.page.admin

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.model.BarChartModel
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.time.LocalDate

@Controller
class StatsStoryController(
    private val kpiService: KpiService,
    private val storyService: StoryService,
    private val readerService: ReaderService,
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
    fun read(
        @RequestParam(name = "story-id") storyId: Long,
    ): BarChartModel =
        kpiService.toBarChartModel(
            kpis = kpiService.search(
                SearchStoryKpiRequest(
                    storyIds = listOf(storyId),
                    types = listOf(KpiType.READ),
                ),
            ),
            type = KpiType.READ,
        )

    @GetMapping("/me/stats/story/chart/read-time")
    @ResponseBody
    fun readTime(
        @RequestParam(name = "story-id") storyId: Long,
    ): BarChartModel =
        kpiService.toBarChartModel(
            kpis = kpiService.search(
                SearchStoryKpiRequest(
                    storyIds = listOf(storyId),
                    types = listOf(KpiType.DURATION),
                ),
            ),
            type = KpiType.DURATION,
        )

    @GetMapping("/me/stats/story/chart/source")
    @ResponseBody
    fun source(
        @RequestParam(name = "story-id") storyId: Long,
        @RequestParam(required = false) period: String? = null,
    ): BarChartModel {
        val toDate = if (period?.lowercase() == "l30") LocalDate.now() else null
        val fromDate = toDate?.let { toDate.minusDays(30) }
        return kpiService.toKpiModelBySource(
            kpis = kpiService.search(
                SearchStoryKpiRequest(
                    storyIds = listOf(storyId),
                    types = listOf(KpiType.READ),
                    dimension = Dimension.SOURCE,
                    fromDate = fromDate,
                    toDate = toDate
                ),
            ),
            type = KpiType.READ,
        )
    }

    @GetMapping("/me/stats/story/readers")
    fun readers(
        @RequestParam(name = "story-id") storyId: Long,
        model: Model,
    ): String {
        val user = requestContext.currentUser()
        if (user != null) {
            val readers = readerService.search(
                SearchReaderRequest(
                    storyId = storyId,
                    subscribedToUserId = user.id,
                    limit = 50,
                ),
            )
            if (readers.isNotEmpty()) {
                model.addAttribute("readers", readers)
            }
        }
        return "admin/fragment/stats-readers"
    }
}
