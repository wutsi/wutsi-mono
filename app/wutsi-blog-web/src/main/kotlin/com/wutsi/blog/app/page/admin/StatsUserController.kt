package com.wutsi.blog.app.page.admin

import com.wutsi.blog.app.model.KpiModel
import com.wutsi.blog.app.model.ReaderModel
import com.wutsi.blog.app.model.SubscriptionModel
import com.wutsi.blog.app.service.KpiService
import com.wutsi.blog.app.service.ReaderService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.service.SubscriptionService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.kpi.dto.Dimension
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import com.wutsi.blog.story.dto.SearchReaderRequest
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/me/stats/user")
class StatsUserController(
    kpiService: KpiService,
    storyService: StoryService,
    readerService: ReaderService,
    requestContext: RequestContext,
    private val subscriptionService: SubscriptionService,
) : AbstractStatsController(kpiService, storyService, readerService, requestContext) {
    override fun pageName() = PageName.STATS_USER

    override fun searchStoryReads(period: String?): List<KpiModel> =
        kpiService.search(
            SearchStoryKpiRequest(
                types = listOf(KpiType.READ),
                dimension = Dimension.ALL,
                fromDate = fromDate(period),
                userId = requestContext.currentUser()!!.id,
            ),
        )

    override fun searchReads(period: String?): List<KpiModel> =
        kpiService.search(
            SearchStoryKpiRequest(
                types = listOf(KpiType.READ),
                userId = requestContext.currentUser()!!.id,
                fromDate = fromDate(period),
            ),
        )

    override fun searchReadTime(period: String?): List<KpiModel> =
        kpiService.search(
            SearchUserKpiRequest(
                userIds = listOf(requestContext.currentUser()!!.id),
                types = listOf(KpiType.DURATION),
                dimension = Dimension.ALL,
                fromDate = fromDate(period),
            ),
        )

    override fun searchSubscriptions(period: String?): List<KpiModel> =
        kpiService.search(
            SearchUserKpiRequest(
                userIds = listOf(requestContext.currentUser()!!.id),
                types = listOf(KpiType.SUBSCRIPTION),
                dimension = Dimension.ALL,
                fromDate = fromDate(period),
            )
        )

    override fun searchSources(period: String?): List<KpiModel> =
        kpiService.search(
            SearchUserKpiRequest(
                userIds = listOf(requestContext.currentUser()!!.id),
                types = listOf(KpiType.READ),
                dimension = Dimension.SOURCE,
                fromDate = fromDate(period),
            ),
        )

    override fun searchReaders(): List<ReaderModel> =
        readerService.search(
            SearchReaderRequest(
                subscribedToUserId = requestContext.currentUser()!!.id,
                limit = 50,
            ),
        )

    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("page", createPage(title = "Statistics", description = ""))
        return "admin/stats-user"
    }

    @GetMapping("/subscribers")
    fun subscribers(@RequestParam(required = false) period: String? = null, model: Model): String {
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


//    @GetMapping("/chart/read")
//    @ResponseBody
//    fun read(@RequestParam(required = false) period: String? = null): BarChartModel =
//        service.toBarChartModel(
//            kpis = service.search(
//                SearchUserKpiRequest(
//                    userIds = listOf(requestContext.currentUser()!!.id),
//                    types = listOf(KpiType.READ),
//                    dimension = Dimension.ALL,
//                    fromDate = fromDate(period),
//                ),
//            ),
//            type = KpiType.READ,
//        )
//
//    @GetMapping("/me/stats/user/chart/read-time")
//    @ResponseBody
//    fun readTime(@RequestParam(required = false) period: String? = null): BarChartModel =
//        service.toBarChartModel(
//            kpis = service.search(
//                SearchUserKpiRequest(
//                    userIds = listOf(requestContext.currentUser()!!.id),
//                    types = listOf(KpiType.DURATION),
//                    dimension = Dimension.ALL,
//                    fromDate = fromDate(period),
//                ),
//            ),
//            type = KpiType.DURATION,
//        )
//
//    @GetMapping("/chart/subscription")
//    @ResponseBody
//    fun subscription(@RequestParam(required = false) period: String? = null): BarChartModel =
//        service.toBarChartModel(
//            kpis = service.search(
//                SearchUserKpiRequest(
//                    userIds = listOf(requestContext.currentUser()!!.id),
//                    types = listOf(KpiType.SUBSCRIPTION),
//                    dimension = Dimension.ALL,
//                    fromDate = fromDate(period),
//                ),
//            ),
//            type = KpiType.SUBSCRIPTION,
//        )
//
//    @GetMapping("/chart/source")
//    @ResponseBody
//    fun source(@RequestParam(required = false) period: String? = null): BarChartModel =
//        service.toKpiModelBySource(
//            kpis = service.search(
//                SearchUserKpiRequest(
//                    userIds = listOf(requestContext.currentUser()!!.id),
//                    types = listOf(KpiType.READ),
//                    dimension = Dimension.SOURCE,
//                    fromDate = fromDate(period),
//                ),
//            ),
//            type = KpiType.READ,
//        )
//    @GetMapping("/me/stats/user/stories")
//    fun stories(@RequestParam(required = false) period: String? = null, model: Model): String {
//        val toDate = if (period == "l30") LocalDate.now() else null
//        val fromDate = toDate?.minusDays(30)
//        val kpis = service.search(
//            SearchStoryKpiRequest(
//                types = listOf(KpiType.READ),
//                userId = requestContext.currentUser()?.id,
//                fromDate = fromDate,
//                toDate = toDate,
//            ),
//        )
//
//        if (kpis.isNotEmpty()) {
//            // Select the top 10 stories with highest read
//            val storyIds = kpis.map { it.targetId }.toSet()
//            val storyIdCountMap = storyIds.map {
//                StoryModel(
//                    id = it,
//                    readCount = computeReadCount(it, kpis)
//                )
//            }.sortedByDescending { it.readCount }
//                .take(10)
//                .associateBy { it.id }
//
//            // Get story details
//            val stories = storyService.search(
//                request = SearchStoryRequest(
//                    storyIds = storyIdCountMap.keys.toList(),
//                    limit = storyIdCountMap.size,
//                    sortBy = StorySortStrategy.NONE,
//                ),
//            ).map {
//                it.copy(readCount = storyIdCountMap[it.id]?.readCount ?: 0)
//            }.sortedByDescending { it.readCount }
//
//            if (stories.isNotEmpty()) {
//                model.addAttribute("stories", stories)
//            }
//        }
//
//        return "admin/fragment/stats-stories"
//    }

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
