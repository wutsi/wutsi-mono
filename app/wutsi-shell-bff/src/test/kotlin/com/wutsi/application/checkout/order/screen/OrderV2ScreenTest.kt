package com.wutsi.application.checkout.order.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.checkout.manager.dto.GetBusinessResponse
import com.wutsi.checkout.manager.dto.GetOrderResponse
import com.wutsi.checkout.manager.dto.UpdateOrderStatusRequest
import com.wutsi.enums.OrderStatus
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

internal class OrderV2ScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val businessId = 333L
    private val business = Fixtures.createBusiness(businessId, MEMBER_ID)
    private val order = Fixtures.createOrder("1111", businessId, MEMBER_ID)

    private fun url(id: String, action: String = "") = "http://localhost:$port${Page.getOrderUrl()}$action?id=$id"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        member = Fixtures.createMember(id = MEMBER_ID, businessId = businessId, business = true)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(anyOrNull())

        doReturn(GetBusinessResponse(business)).whenever(checkoutManagerApi).getBusiness(any())

        doReturn(GetOrderResponse(order)).whenever(checkoutManagerApi).getOrder(any())
    }

    @Test
    fun index() {
        assertEndpointEquals("/checkout/order/screens/order.json", url("1111"))
    }

    @Test
    fun cancel() {
        val response = rest.postForEntity(url(order.id, "/cancel"), null, Action::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals("http://localhost:0${Page.getOrderUrl()}", action.url)
        assertEquals(mapOf("id" to order.id), action.parameters)
        assertEquals(ActionType.Route, action.type)

        verify(checkoutManagerApi).updateOrderStatus(UpdateOrderStatusRequest(order.id, OrderStatus.CANCELLED.name))
    }

    @Test
    fun accept() {
        val response = rest.postForEntity(url(order.id, "/accept"), null, Action::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals("http://localhost:0${Page.getOrderUrl()}", action.url)
        assertEquals(mapOf("id" to order.id), action.parameters)
        assertEquals(ActionType.Route, action.type)

        verify(checkoutManagerApi).updateOrderStatus(UpdateOrderStatusRequest(order.id, OrderStatus.IN_PROGRESS.name))
    }

    @Test
    fun complete() {
        val response = rest.postForEntity(url(order.id, "/complete"), null, Action::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals("http://localhost:0${Page.getOrderUrl()}", action.url)
        assertEquals(mapOf("id" to order.id), action.parameters)
        assertEquals(ActionType.Route, action.type)

        verify(checkoutManagerApi).updateOrderStatus(UpdateOrderStatusRequest(order.id, OrderStatus.COMPLETED.name))
    }
}
