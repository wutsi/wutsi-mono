package com.wutsi.blog.app.page.admin

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.model.BarChartModel
import com.wutsi.blog.app.model.BarChartSerieModel
import com.wutsi.blog.app.model.KpiModel
import com.wutsi.blog.app.service.KpiService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Controller
class StatsStoryController(
    private val service: KpiService,
    private val storyService: StoryService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.STATS_STORY

    @GetMapping("/me/stats/story")
    fun story(
        @RequestParam(name = "story-id") id: Long,
        model: Model,
    ): String {
        val story = storyService.get(id)

        model.addAttribute("story", story)
        model.addAttribute("page", createPage(title = "Statistics", description = ""))
        return "admin/stats-story"
    }

    @GetMapping("/me/stats/story-chart")
    @ResponseBody
    fun storyChart(
        @RequestParam(name = "story-id") id: Long,
        @RequestParam type: KpiType,
    ): BarChartModel =
        toKpiModel(
            kpis = service.search(
                SearchStoryKpiRequest(
                    storyIds = listOf(id),
                    types = listOf(type),
                )
            ),
            type = type,
        )

    @GetMapping("/me/stats/user")
    fun user(
        model: Model,
    ): String {
        model.addAttribute("page", createPage(title = "Statistics", description = ""))
        return "admin/stats-user"
    }

    @GetMapping("/me/stats/user-chart")
    @ResponseBody
    fun userChart(
        @RequestParam type: KpiType,
    ): BarChartModel =
        toKpiModel(
            kpis = service.search(
                SearchUserKpiRequest(
                    userIds = listOf(requestContext.currentUser()!!.id),
                    types = listOf(type),
                )
            ),
            type = type,
        )

    private fun toKpiModel(kpis: List<KpiModel>, type: KpiType): BarChartModel {
        val kpiByDate = kpis.associateBy { it.date }
        val categoryByDate = toBarCharCategories(kpiByDate.keys.toList())
        val fmt = DateTimeFormatter.ofPattern("MMM yyyy", LocaleContextHolder.getLocale())
        return BarChartModel(
            categories = categoryByDate.map { it.format(fmt) },
            series = listOf(
                BarChartSerieModel(
                    name = type.name,
                    data = categoryByDate.map {
                        (kpiByDate[it]?.value ?: 0).toDouble()
                    }
                )
            )
        )
    }

    private fun toBarCharCategories(dates: List<LocalDate>): List<LocalDate> {
        if (dates.isEmpty()) {
            return emptyList()
        } else if (dates.size == 1) {
            return dates
        } else {
            val sorted = dates.sorted()
            val first = sorted.first()
            val last = sorted.last()

            var cur = first
            val series = mutableListOf<LocalDate>()
            while (cur.isBefore(last) || cur.isEqual(last)) {
                series.add(cur)
                cur = cur.plusMonths(1)
            }
            return series
        }
    }
}
