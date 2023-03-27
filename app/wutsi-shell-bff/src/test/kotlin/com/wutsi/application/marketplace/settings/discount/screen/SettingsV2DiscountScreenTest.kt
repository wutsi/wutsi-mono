package com.wutsi.application.marketplace.settings.discount.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.marketplace.manager.dto.GetDiscountResponse
import com.wutsi.marketplace.manager.dto.UpdateDiscountAttributeRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

internal class SettingsV2DiscountScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url(id: Long, action: String = "") =
        "http://localhost:$port${Page.getSettingsDiscountUrl()}$action?id=$id"

    @Test
    fun `sales to all products`() {
        val discount = Fixtures.createDiscount(
            id = 111L,
            allProduct = true,
        )
        doReturn(GetDiscountResponse(discount)).whenever(marketplaceManagerApi).getDiscount(any())

        assertEndpointEquals("/marketplace/settings/discount/screens/discount-all-products.json", url(111L))
    }

    @Test
    fun `sales to some products`() {
        val discount = Fixtures.createDiscount(
            id = 111L,
            allProduct = false,
            productIds = listOf(100L, 200L),
        )
        doReturn(GetDiscountResponse(discount)).whenever(marketplaceManagerApi).getDiscount(any())

        assertEndpointEquals("/marketplace/settings/discount/screens/discount-some-products.json", url(111L))
    }

    @Test
    fun `apply to some products`() {
        val response = rest.postForEntity(url(100, "/apply-to") + "&value=false", null, Action::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals("http://localhost:0${Page.getSettingsDiscountUrl()}", action.url)
        assertEquals(mapOf("id" to "100"), action.parameters)

        verify(marketplaceManagerApi).updateDiscountAttribute(
            100L,
            UpdateDiscountAttributeRequest("all-products", "false"),
        )
    }

    @Test
    fun `apply to all products`() {
        val response = rest.postForEntity(url(100, "/apply-to") + "&value=true", null, Action::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals("http://localhost:0${Page.getSettingsDiscountUrl()}", action.url)
        assertEquals(mapOf("id" to "100"), action.parameters)

        verify(marketplaceManagerApi).updateDiscountAttribute(
            100L,
            UpdateDiscountAttributeRequest("all-products", "true"),
        )
    }
}
