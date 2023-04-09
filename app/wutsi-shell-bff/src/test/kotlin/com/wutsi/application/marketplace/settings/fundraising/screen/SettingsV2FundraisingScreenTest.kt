package com.wutsi.application.marketplace.settings.fundraising.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.marketplace.manager.dto.GetFundraisingResponse
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class SettingsV2FundraisingScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val fundraising = Fixtures.createFundraising()

    private fun url() = "http://localhost:$port${Page.getSettingsFundraisingUrl()}"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        member = Fixtures.createMember(id = MEMBER_ID, fundraisingId = 555L)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(anyOrNull())

        doReturn(GetFundraisingResponse(fundraising)).whenever(marketplaceManagerApi).getFundraising(any())
    }

    @Test
    fun index() = assertEndpointEquals("/marketplace/settings/fundraising/screens/index.json", url())
}
