package com.wutsi.blog.app.page.admin

import com.wutsi.blog.app.model.BarChartModel
import com.wutsi.blog.app.model.KpiModel
import com.wutsi.blog.app.model.ReaderModel
import com.wutsi.blog.app.model.SubscriptionModel
import com.wutsi.blog.app.service.KpiService
import com.wutsi.blog.app.service.ReaderService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.service.SubscriptionService
import com.wutsi.blog.app.service.SuperFanService
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
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/me/stats/user")
class StatsUserController(
    kpiService: KpiService,
    storyService: StoryService,
    readerService: ReaderService,
    requestContext: RequestContext,
    private val subscriptionService: SubscriptionService,
    private val superFanService: SuperFanService,
) : AbstractStatsController(kpiService, storyService, readerService, requestContext) {
    override fun pageName() = PageName.STATS_USER

    override fun searchStoryKpis(period: String?, types: List<KpiType>): List<KpiModel> =
        kpiService.search(
            SearchStoryKpiRequest(
                types = types,
                dimension = Dimension.ALL,
                fromDate = fromDate(period),
                userId = requestContext.currentUser()!!.id,
            ),
        )

    override fun searchReads(period: String?): List<KpiModel> =
        kpiService.search(
            SearchUserKpiRequest(
                types = listOf(KpiType.READ),
                userIds = listOf(requestContext.currentUser()!!.id),
                fromDate = fromDate(period),
                dimension = Dimension.ALL,
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

    override fun searchClicks(period: String?): List<KpiModel> =
        emptyList()

    override fun searchReaders(limit: Int, offset: Int): List<ReaderModel> =
        readerService.search(
            SearchReaderRequest(
                subscribedToUserId = requestContext.currentUser()!!.id,
                limit = limit,
                offset = offset,
            ),
        )

    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("page", createPage(title = "Statistics", description = ""))

        val user = getUser()
        user?.let {
            model.addAttribute("wallet", getWallet(user))
            model.addAttribute("store", requestContext.currentStore())
        }

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

    @GetMapping("/chart/wpp")
    @ResponseBody
    fun user(@RequestParam(required = false) period: String? = null): BarChartModel =
        kpiService.toBarChartModel(
            kpis = kpiService.search(
                SearchUserKpiRequest(
                    userIds = listOf(requestContext.currentUser()!!.id),
                    types = listOf(KpiType.WPP_EARNING, KpiType.WPP_BONUS),
                    dimension = Dimension.ALL,
                    fromDate = fromDate(period),
                ),
            )
        )

    @GetMapping("/chart/revenus")
    @ResponseBody
    fun revenus(@RequestParam(required = false) period: String? = null): BarChartModel =
        kpiService.toBarChartModel(
            kpis = kpiService.search(
                SearchUserKpiRequest(
                    userIds = listOf(requestContext.currentUser()!!.id),
                    types = listOf(KpiType.SALES_VALUE, KpiType.DONATION_VALUE),
                    dimension = Dimension.ALL,
                    fromDate = fromDate(period),
                ),
            ),
        )

    @GetMapping("/super-fans")
    fun superFans(
        @RequestParam(required = false) offset: Int? = null,
        model: Model,
    ): String {
        val user = requestContext.currentUser()
        if (user != null) {
            val wallet = getWallet(user)
            if (wallet != null) {
                val superFans = superFanService.search(wallet)
                model.addAttribute("superFans", superFans)
            }
        }
        return "admin/fragment/stats-super-fans"
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
