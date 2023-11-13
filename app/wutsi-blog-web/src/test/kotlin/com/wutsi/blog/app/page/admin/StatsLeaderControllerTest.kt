package com.wutsi.blog.app.page.admin

import com.nhaarman.mockitokotlin2.any
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
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.UserSummary
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn

internal class StatsLeaderControllerTest : SeleniumTestSupport() {
    private val userKpis = listOf(
        UserKpi(userId = 10, type = KpiType.READ, source = TrafficSource.EMAIL, year = 2020, month = 1, value = 100),
        UserKpi(userId = 11, type = KpiType.READ, source = TrafficSource.DIRECT, year = 2020, month = 2, value = 110),
        UserKpi(userId = 12, type = KpiType.READ, source = TrafficSource.LINKEDIN, year = 2020, month = 3, value = 120),
    )

    private val storyKpis = listOf(
        StoryKpi(storyId = 10, type = KpiType.READ, year = 2020, month = 1, value = 100),
        StoryKpi(storyId = 11, year = 2020, month = 2, value = 110),
        StoryKpi(storyId = 12, type = KpiType.READ, year = 2020, month = 3, value = 120),
    )

    private val stories = listOf(
        StorySummary(id = 10, userId = 10),
        StorySummary(id = 11, userId = 11),
        StorySummary(id = 12, userId = 12)
    )

    private val users = listOf(
        UserSummary(id = 10, name = "user10", fullName = "User #10"),
        UserSummary(id = 11, name = "user11", fullName = "User #11"),
        UserSummary(id = 12, name = "user12", fullName = "User #12"),
    )

    @Test
    fun index() {
        // GIVEN
        setupLoggedInUser(100, superUser = true)
        doReturn(SearchUserKpiResponse(userKpis)).whenever(kpiBackend).search(any<SearchUserKpiRequest>())
        doReturn(SearchStoryKpiResponse(storyKpis)).whenever(kpiBackend).search(any<SearchStoryKpiRequest>())

        doReturn(SearchStoryResponse(stories)).whenever(storyBackend).search(any())
        doReturn(SearchUserResponse(users)).whenever(userBackend).search(any())

        // WHEN
        navigate(url("/me/stats/leader"))

        // THEN
        assertCurrentPageIs(PageName.STATS_LEADER)
    }
}
