package com.wutsi.application.membership.profile.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.marketplace.manager.dto.SearchProductResponse
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class ProfileV2ScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url(qs: String = "") = "http://localhost:$port${Page.getProfileUrl()}?$qs"

    @Test
    fun personal() = assertEndpointEquals("/membership/profile/screens/personal.json", url())

    @Test
    fun business() {
        val business = Fixtures.createMember(business = true, storeId = 11L)
        doReturn(GetMemberResponse(business)).whenever(membershipManagerApi).getMember(any())

        val products = listOf(
            Fixtures.createProductSummary(1L, title = "Product1", price = 10000),
            Fixtures.createProductSummary(2L, title = "Product2", price = 11000),
        )
        doReturn(SearchProductResponse(products)).whenever(marketplaceManagerApi).searchProduct(any())

        assertEndpointEquals("/membership/profile/screens/business.json", url())
    }

    @Test
    fun businessNoStore() {
        val business = Fixtures.createMember(id = MEMBER_ID, business = true, storeId = null)
        doReturn(GetMemberResponse(business)).whenever(membershipManagerApi).getMember(any())

        assertEndpointEquals("/membership/profile/screens/business-no-store.json", url())
    }
}
