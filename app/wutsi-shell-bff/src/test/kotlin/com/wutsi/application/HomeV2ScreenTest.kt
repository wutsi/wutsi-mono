package com.wutsi.application

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.checkout.manager.dto.GetBusinessResponse
import com.wutsi.checkout.manager.dto.SearchOrderResponse
import com.wutsi.enums.OrderStatus
import com.wutsi.error.ErrorURN
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class HomeV2ScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port${Page.getHomeUrl()}"

    @Test
    fun index() = assertEndpointEquals("/home/screens/index.json", url())

    @Test
    fun businessNoStore() {
        // GIVEN
        val businessId = 5555L
        member = Fixtures.createMember(MEMBER_ID, businessId = businessId, business = true, storeId = null)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(MEMBER_ID)

        val business = Fixtures.createBusiness(businessId, MEMBER_ID)
        doReturn(GetBusinessResponse(business)).whenever(checkoutManagerApi).getBusiness(any())

        val orders = listOf(
            Fixtures.createOrderSummary(id = "11", totalPrice = 15000, status = OrderStatus.OPENED),
            Fixtures.createOrderSummary(id = "22", totalPrice = 25000, status = OrderStatus.OPENED),
        )
        doReturn(SearchOrderResponse(orders)).whenever(checkoutManagerApi).searchOrder(any())

        // THEN
        assertEndpointEquals("/home/screens/business-no-store.json", url())
    }

    @Test
    fun businessWithStore() {
        // GIVEN
        val businessId = 5555L
        member = Fixtures.createMember(MEMBER_ID, businessId = businessId, business = true, storeId = 3333)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(MEMBER_ID)

        val business = Fixtures.createBusiness(businessId, MEMBER_ID)
        doReturn(GetBusinessResponse(business)).whenever(checkoutManagerApi).getBusiness(any())

        val orders = listOf(
            Fixtures.createOrderSummary(id = "11", totalPrice = 15000, status = OrderStatus.OPENED),
            Fixtures.createOrderSummary(id = "22", totalPrice = 25000, status = OrderStatus.OPENED),
        )
        doReturn(SearchOrderResponse(orders)).whenever(checkoutManagerApi).searchOrder(any())

        // THEN
        assertEndpointEquals("/home/screens/business-store.json", url())
    }

    @Test
    fun unsupportedCountry() {
        // GIVEN
        val businessId = 5555L
        member = Fixtures.createMember(MEMBER_ID, business = false, country = "CA")
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(MEMBER_ID)

        val business = Fixtures.createBusiness(businessId, MEMBER_ID)
        doReturn(GetBusinessResponse(business)).whenever(checkoutManagerApi).getBusiness(any())

        val orders = listOf(
            Fixtures.createOrderSummary(id = "11", totalPrice = 15000, status = OrderStatus.OPENED),
            Fixtures.createOrderSummary(id = "22", totalPrice = 25000, status = OrderStatus.OPENED),
        )
        doReturn(SearchOrderResponse(orders)).whenever(checkoutManagerApi).searchOrder(any())

        // THEN
        assertEndpointEquals("/home/screens/country-not-supported.json", url())
    }

    @Test
    fun `redirect on onboard page if member not found`() {
        val ex = createNotFoundException(errorCode = ErrorURN.MEMBER_NOT_FOUND.urn)
        doThrow(ex).whenever(membershipManagerApi).getMember(any())

        assertEndpointEquals("/membership/onboard/screens/index.json", url())
    }
}
