package com.wutsi.blog.app.page.admin

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.model.BarChartModel
import com.wutsi.blog.app.model.KpiModel
import com.wutsi.blog.app.service.KpiService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import com.wutsi.blog.story.dto.SearchStoryRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.time.LocalDate

@Controller
class StatsController(
    private val service: KpiService,
    private val storyService: StoryService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.STATS_STORY

    @GetMapping("/me/stats")
    fun index(model: Model): String {
        model.addAttribute("page", createPage(title = "Statistics", description = ""))

        val today = LocalDate.now()
        val kpis = service.search(
            SearchStoryKpiRequest(
                types = listOf(KpiType.READ),
                userId = requestContext.currentUser()?.id,
                fromDate = today.minusMonths(2),
            ),
        )
        if (kpis.isNotEmpty()) {
            val storyIds = kpis.map { it.targetId }.toSet().toList()
            val stories = storyService.search(
                request = SearchStoryRequest(
                    storyIds = storyIds,
                    limit = storyIds.size,
                ),
            ).map { it.copy(readCount = computeReadCount(it.id, kpis)) }.sortedByDescending { it.readCount }.take(10)
            model.addAttribute("stories", stories)
        }
        return "admin/stats-user"
    }

    private fun computeReadCount(storyId: Long, kpis: List<KpiModel>): Long {
        val filtered = kpis.filter { it.targetId == storyId }
        return filtered.reduce { acc, kpi -> acc.copy(value = acc.value + kpi.value) }
            .value
    }

    @GetMapping("/me/stats/chart")
    @ResponseBody
    fun userChart(@RequestParam type: KpiType): BarChartModel =
        service.toKpiModel(
            kpis = service.search(
                SearchUserKpiRequest(
                    userIds = listOf(requestContext.currentUser()!!.id),
                    types = listOf(type),
                ),
            ),
            type = type,
        )
}
