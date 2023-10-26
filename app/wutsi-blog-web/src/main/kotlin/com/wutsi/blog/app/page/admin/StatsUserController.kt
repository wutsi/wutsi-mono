package com.wutsi.blog.app.page.admin

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.model.BarChartModel
import com.wutsi.blog.app.model.KpiModel
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.SubscriptionModel
import com.wutsi.blog.app.service.KpiService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.service.SubscriptionService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.kpi.dto.Dimension
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.time.LocalDate

@Controller
class StatsUserController(
    private val service: KpiService,
    private val storyService: StoryService,
    private val subscriptionService: SubscriptionService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.STATS_USER

    @GetMapping("/me/stats/user")
    fun index(model: Model): String {
        model.addAttribute("page", createPage(title = "Statistics", description = ""))
        return "admin/stats-user"
    }

    private fun computeReadCount(storyId: Long, kpis: List<KpiModel>): Long {
        val filtered = kpis.filter { it.targetId == storyId }
        return filtered.reduce { acc, kpi -> acc.copy(value = acc.value + kpi.value) }
            .value
    }

    @GetMapping("/me/stats/user/chart/read")
    @ResponseBody
    fun read(): BarChartModel =
        service.toBarChartModel(
            kpis = service.search(
                SearchUserKpiRequest(
                    userIds = listOf(requestContext.currentUser()!!.id),
                    types = listOf(KpiType.READ),
                    dimension = Dimension.ALL,
                ),
            ),
            type = KpiType.READ,
        )

    @GetMapping("/me/stats/user/chart/read-time")
    @ResponseBody
    fun readTime(): BarChartModel =
        service.toBarChartModel(
            kpis = service.search(
                SearchUserKpiRequest(
                    userIds = listOf(requestContext.currentUser()!!.id),
                    types = listOf(KpiType.DURATION),
                    dimension = Dimension.ALL,
                ),
            ),
            type = KpiType.DURATION,
        )

    @GetMapping("/me/stats/user/chart/subscription")
    @ResponseBody
    fun subscription(): BarChartModel =
        service.toBarChartModel(
            kpis = service.search(
                SearchUserKpiRequest(
                    userIds = listOf(requestContext.currentUser()!!.id),
                    types = listOf(KpiType.SUBSCRIPTION),
                    dimension = Dimension.ALL,
                ),
            ),
            type = KpiType.SUBSCRIPTION,
        )

    @GetMapping("/me/stats/user/chart/source")
    @ResponseBody
    fun source(@RequestParam(required = false) period: String? = null): BarChartModel {
        val toDate = if (period == "l30") LocalDate.now() else null
        val fromDate = toDate?.minusDays(30)
        return service.toKpiModelBySource(
            kpis = service.search(
                SearchUserKpiRequest(
                    userIds = listOf(requestContext.currentUser()!!.id),
                    types = listOf(KpiType.READ),
                    dimension = Dimension.SOURCE,
                    fromDate = fromDate,
                    toDate = toDate,
                ),
            ),
            type = KpiType.READ,
        )
    }

    @GetMapping("/me/stats/user/subscribers")
    fun subscribers(model: Model): String {
        val user = requestContext.currentUser()
        if (user != null) {
            val subscriptions = subscriptionService.search(
                request = SearchSubscriptionRequest(
                    userIds = listOf(user.id),
                    limit = 50,
                ),
                withUser = true,
            )

            if (subscriptions.isNotEmpty()) {
                model.addAttribute("icons", toIcons(filterWithPicture(subscriptions), 5))
                model.addAttribute("subscriptions", filterWithNameOrEmail(subscriptions))
            }
        }
        return "admin/fragment/stats-subscribers"
    }

    @GetMapping("/me/stats/user/stories")
    fun stories(@RequestParam(required = false) period: String? = null, model: Model): String {
        val toDate = if (period == "l30") LocalDate.now() else null
        val fromDate = toDate?.minusDays(30)
        val kpis = service.search(
            SearchStoryKpiRequest(
                types = listOf(KpiType.READ),
                userId = requestContext.currentUser()?.id,
                fromDate = fromDate,
                toDate = toDate,
            ),
        )

        if (kpis.isNotEmpty()) {
            // Select the top 10 stories with highest read
            val storyIds = kpis.map { it.targetId }.toSet()
            val storyIdCountMap = storyIds.map {
                StoryModel(
                    id = it,
                    readCount = computeReadCount(it, kpis)
                )
            }.sortedByDescending { it.readCount }
                .take(10)
                .associateBy { it.id }

            // Get story details
            val stories = storyService.search(
                request = SearchStoryRequest(
                    storyIds = storyIdCountMap.keys.toList(),
                    limit = storyIdCountMap.size,
                    sortBy = StorySortStrategy.NONE,
                ),
            ).map {
                it.copy(readCount = storyIdCountMap[it.id]?.readCount ?: 0)
            }.sortedByDescending { it.readCount }

            if (stories.isNotEmpty()) {
                model.addAttribute("stories", stories)
            }
        }

        return "admin/fragment/stats-stories"
    }

    private fun filterWithPicture(subscriptions: List<SubscriptionModel>) =
        subscriptions.filter { !it.subscriber.pictureUrl.isNullOrEmpty() }

    private fun filterWithNameOrEmail(subscriptions: List<SubscriptionModel>) =
        subscriptions.filter {
            !(it.subscriber.fullName.isEmpty() && it.subscriber.email.isNullOrEmpty())
        }

    private fun toIcons(subscriptions: List<SubscriptionModel>, n: Int): List<SubscriptionModel> {
        val result = mutableListOf<SubscriptionModel>()
        result.addAll(subscriptions.filter { it.subscriber.blog }.take(n))
        if (result.size < n) {
            val m = n - result.size
            result.addAll(subscriptions.filter { !it.subscriber.blog }.take(n - m))
        }
        return result
    }
}
