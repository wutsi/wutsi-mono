package com.wutsi.blog.app.page.admin

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.dto.SearchStoryKpiResponse
import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import com.wutsi.blog.kpi.dto.SearchUserKpiResponse
import com.wutsi.blog.kpi.dto.StoryKpi
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.kpi.dto.UserKpi
import com.wutsi.blog.story.dto.SearchStoryResponse
import com.wutsi.blog.story.dto.StorySummary
import com.wutsi.blog.subscription.dto.SearchSubscriptionResponse
import com.wutsi.blog.subscription.dto.Subscription
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.UserSummary
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

internal class StatsUserControllerTest : SeleniumTestSupport() {
    private val userKpis = listOf(
        UserKpi(userId = 10, type = KpiType.READ, source = TrafficSource.EMAIL, year = 2020, month = 1, value = 100),
        UserKpi(userId = 10, type = KpiType.READ, source = TrafficSource.DIRECT, year = 2020, month = 2, value = 110),
        UserKpi(userId = 10, type = KpiType.READ, source = TrafficSource.LINKEDIN, year = 2020, month = 3, value = 120),
    )

    private val storyKpis = listOf(
        StoryKpi(storyId = 10, type = KpiType.READ, source = TrafficSource.DIRECT, year = 2020, month = 1, value = 100),
        StoryKpi(storyId = 10, year = 2020, month = 2, value = 110),
        StoryKpi(storyId = 10, type = KpiType.READ, year = 2020, month = 3, value = 120),
    )

    private val subscriptions = listOf(
        Subscription(userId = 100, subscriberId = 101, subscriptionDateTime = Date()),
        Subscription(userId = 100, subscriberId = 102, subscriptionDateTime = DateUtils.addDays(Date(), -1)),
        Subscription(userId = 100, subscriberId = 103, subscriptionDateTime = DateUtils.addDays(Date(), -2)),
        Subscription(userId = 100, subscriberId = 104, subscriptionDateTime = DateUtils.addDays(Date(), -3)),
        Subscription(userId = 100, subscriberId = 105, subscriptionDateTime = DateUtils.addDays(Date(), -4)),
    )

    private val subscribers = listOf(
        UserSummary(
            id = 101,
            name = "yo.man",
            blog = true,
            fullName = "Yo",
            pictureUrl = "https://picsum.photos/50/50",
        ),
        UserSummary(
            id = 102,
            name = "user.102",
            blog = true,
            fullName = "User 102",
            pictureUrl = "https://picsum.photos/50/50",
        ),
        UserSummary(id = 103, name = "user.103", fullName = "User 103", pictureUrl = "https://picsum.photos/50/50"),
        UserSummary(id = 104, name = "user.103", fullName = "User 104", pictureUrl = "https://picsum.photos/50/50"),
        UserSummary(id = 105, name = "user.103", fullName = "User 104", pictureUrl = "https://picsum.photos/50/50"),
    )

    private val stories = listOf(
        StorySummary(id = 10, title = "Story 10")
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        setupLoggedInUser(100)
        doReturn(SearchStoryResponse(stories)).whenever(storyBackend).search(any())
        doReturn(SearchUserKpiResponse(userKpis)).whenever(kpiBackend).search(any<SearchUserKpiRequest>())
        doReturn(SearchStoryKpiResponse(storyKpis)).whenever(kpiBackend).search(any<SearchStoryKpiRequest>())

        doReturn(SearchSubscriptionResponse(subscriptions)).whenever(subscriptionBackend).search(any())
        doReturn(SearchUserResponse(subscribers)).whenever(userBackend).search(any())
    }

    @Test
    fun index() {
        // WHEN
        navigate(url("/me/stats/user"))
        Thread.sleep(5000)

        // THEN
        assertCurrentPageIs(PageName.STATS_USER)

        assertElementPresent("#kpi-overview-read")
        assertElementVisible("#kpi-overview-subscriber")

        // Read
        assertElementVisible("#chart-area-read")

        // Traffic
        click("#nav-traffic-tab")
        assertElementVisible("#chart-area-traffic")
        click("#pill-traffic-overall")

        // Subscribers
        click("#nav-subscription-tab")
        assertElementVisible(".user-picture-set")
        click(".user-picture-set a")
        assertElementVisible("#subscriber-modal")
    }
}
