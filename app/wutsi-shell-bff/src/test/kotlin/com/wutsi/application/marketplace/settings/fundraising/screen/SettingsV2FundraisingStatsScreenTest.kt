package com.wutsi.application.marketplace.settings.fundraising.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.checkout.manager.dto.GetBusinessResponse
import com.wutsi.checkout.manager.dto.SearchDonationKpiResponse
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset

internal class SettingsV2FundraisingStatsScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var clock: Clock

    private fun url() = "http://localhost:$port${Page.getSettingsFundraisingStatsUrl()}"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        val stats = listOf(
            Fixtures.createDonationKpiSummary(),
        )
        doReturn(SearchDonationKpiResponse(stats)).whenever(checkoutManagerApi).searchDonationKpi(any())

        val member = Fixtures.createMember(id = MEMBER_ID, business = true, businessId = 111L)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(any())

        val business = Fixtures.createBusiness(id = 111L, accountId = MEMBER_ID)
        doReturn(GetBusinessResponse(business)).whenever(checkoutManagerApi).getBusiness(any())

        val today = LocalDate.of(2020, 2, 1)
        doReturn(today.atStartOfDay().toInstant(ZoneOffset.UTC)).whenever(clock).instant()
    }

    @Test
    fun index() = assertEndpointEquals("/marketplace/settings/fundraising/screens/stats.json", url())
}
