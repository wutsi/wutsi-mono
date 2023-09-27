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
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn

internal class StatsControllerTest : SeleniumTestSupport() {
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

    @Test
    fun index() {
        // GIVEN
        setupLoggedInUser(100, superUser = true)
        doReturn(SearchUserKpiResponse(userKpis)).whenever(kpiBackend).search(any<SearchUserKpiRequest>())
        doReturn(SearchStoryKpiResponse(storyKpis)).whenever(kpiBackend).search(any<SearchStoryKpiRequest>())

        // WHEN
        navigate(url("/me/stats"))

        // THEN
        assertCurrentPageIs(PageName.STATS)

        assertElementPresent("#chart-area-read")
        assertElementPresent("#chart-area-traffic")
    }
}
