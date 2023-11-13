package com.wutsi.blog.app.page.admin

import com.wutsi.blog.app.model.KpiModel
import com.wutsi.blog.app.model.ReaderModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.service.KpiService
import com.wutsi.blog.app.service.ReaderService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.kpi.dto.Dimension
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import com.wutsi.blog.user.dto.SearchUserRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/me/stats/leader")
class StatsLeaderController(
    kpiService: KpiService,
    storyService: StoryService,
    readerService: ReaderService,
    requestContext: RequestContext,
    private val userService: UserService,
) : AbstractStatsController(kpiService, storyService, readerService, requestContext) {
    override fun pageName() = PageName.STATS_LEADER

    override fun searchClicks(period: String?) = emptyList<KpiModel>()

    override fun searchReadTime(period: String?) = emptyList<KpiModel>()

    override fun searchSubscriptions(period: String?) = emptyList<KpiModel>()

    override fun searchReads(period: String?) = emptyList<KpiModel>()

    override fun searchReaders(limit: Int, offset: Int) = emptyList<ReaderModel>()

    override fun searchSources(period: String?) = emptyList<KpiModel>()

    override fun searchStoryKpis(period: String?, types: List<KpiType>): List<KpiModel> =
        kpiService.search(
            SearchStoryKpiRequest(
                types = types,
                dimension = Dimension.ALL,
                fromDate = fromDate(period),
            ),
        )

    override fun maxStories(): Int = 50

    fun maxWriters(): Int = 20
    private fun searchUserKpis(period: String?, types: List<KpiType>): List<KpiModel> =
        kpiService.search(
            SearchUserKpiRequest(
                types = types,
                dimension = Dimension.ALL,
                fromDate = fromDate(period),
            ),
        )

    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("page", createPage(title = "Statistics", description = ""))
        return "admin/stats-leader"
    }

    @GetMapping("/writers")
    fun writers(@RequestParam(required = false) period: String? = null, model: Model): String {
        val kpis = searchUserKpis(
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
            val userIds = kpis.map { it.targetId }.toSet()
            val userIdCountMap = userIds.map {
                UserModel(
                    id = it,
                    readCount = sum(it, kpis, KpiType.READ),
                    totalDurationSeconds = sum(it, kpis, KpiType.DURATION),
                    clickCount = avg(it, kpis, KpiType.CLICK),
                )
            }.sortedByDescending { it.readCount }
                .take(maxWriters())
                .associateBy { it.id }

            // Get story details
            val tmp = userService.search(
                request = SearchUserRequest(
                    userIds = userIdCountMap.keys.toList(),
                    limit = userIdCountMap.size,
                ),
            )
            val writers = tmp.map {
                if (period == null) {
                    it
                } else {
                    it.copy(
                        readCount = userIdCountMap[it.id]?.readCount ?: 0,
                        totalDurationSeconds = userIdCountMap[it.id]?.totalDurationSeconds ?: 0,
                        clickCount = userIdCountMap[it.id]?.clickCount ?: 0,
                    )
                }
            }.sortedByDescending { it.totalDurationSeconds }

            if (writers.isNotEmpty()) {
                model.addAttribute("writers", writers)
            }
        }

        return "admin/fragment/stats-writers"
    }
}
