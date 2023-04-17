package com.wutsi.application.checkout.order.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.checkout.manager.dto.GetBusinessResponse
import com.wutsi.checkout.manager.dto.SearchOrderResponse
import com.wutsi.enums.OrderStatus
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class DonationListScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port${Page.getDonationListUrl()}"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        member = Fixtures.createMember(id = MEMBER_ID, businessId = 333, business = true, fundraisingId = 555)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(anyOrNull())

        val business = Fixtures.createBusiness(member.businessId!!, member.id)
        doReturn(GetBusinessResponse(business)).whenever(checkoutManagerApi).getBusiness(any())

        val orders = listOf(
            Fixtures.createOrderSummary("111", 1000, OrderStatus.OPENED),
            Fixtures.createOrderSummary("200", 0, OrderStatus.OPENED),
            Fixtures.createOrderSummary("300", 1000, OrderStatus.CANCELLED),
            Fixtures.createOrderSummary("400", 1500, OrderStatus.COMPLETED),
        )
        doReturn(SearchOrderResponse(orders)).whenever(checkoutManagerApi).searchOrder(any())
    }

    @Test
    fun index() {
        assertEndpointEquals("/checkout/order/screens/donation-list.json", url())
    }
}
