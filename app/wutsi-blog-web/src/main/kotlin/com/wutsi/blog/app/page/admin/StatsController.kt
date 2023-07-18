package com.wutsi.blog.app.page.admin

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.model.BarChartModel
import com.wutsi.blog.app.service.KpiService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class StatsController(
    private val service: KpiService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.STATS_STORY

    @GetMapping("/me/stats")
    fun index(model: Model): String {
        model.addAttribute("page", createPage(title = "Statistics", description = ""))
        return "admin/stats-user"
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
