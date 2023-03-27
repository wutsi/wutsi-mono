package com.wutsi.application.marketplace.settings.discount.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.application.marketplace.settings.product.dto.SubmitAttributeRequest
import com.wutsi.marketplace.manager.dto.GetDiscountResponse
import com.wutsi.marketplace.manager.dto.UpdateDiscountAttributeRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

internal class SettingsV2DiscountEditorScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val discountId: Long = 100

    private fun url(name: String, action: String = "") =
        "http://localhost:$port${Page.getSettingsDiscountEditorUrl()}$action?id=$discountId&name=$name"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        val discount = Fixtures.createDiscount(discountId)
        doReturn(GetDiscountResponse(discount)).whenever(marketplaceManagerApi).getDiscount(any())
    }

    @Test
    fun name() {
        assertEndpointEquals("/marketplace/settings/discount/screens/editor-name.json", url("name"))
    }

    @Test
    fun submitName() {
        val name = "name"
        val request = SubmitAttributeRequest("FIN25")
        val response = rest.postForEntity(url(name, "/submit"), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        verify(marketplaceManagerApi).updateDiscountAttribute(
            discountId,
            UpdateDiscountAttributeRequest(name, request.value),
        )
    }

    @Test
    fun rate() {
        assertEndpointEquals("/marketplace/settings/discount/screens/editor-rate.json", url("rate"))
    }

    @Test
    fun submitRate() {
        val name = "rate"
        val request = SubmitAttributeRequest("25")
        val response = rest.postForEntity(url(name, "/submit"), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        verify(marketplaceManagerApi).updateDiscountAttribute(
            discountId,
            UpdateDiscountAttributeRequest(name, request.value),
        )
    }

    @Test
    fun starts() {
        assertEndpointEquals("/marketplace/settings/discount/screens/editor-starts.json", url("starts"))
    }

    @Test
    fun submitStarts() {
        val name = "starts"
        val request = SubmitAttributeRequest("2020-03-13")
        val response = rest.postForEntity(url(name, "/submit"), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        verify(marketplaceManagerApi).updateDiscountAttribute(
            discountId,
            UpdateDiscountAttributeRequest(name, "2020-03-13 00:00:00"),
        )
    }

    @Test
    fun ends() {
        assertEndpointEquals("/marketplace/settings/discount/screens/editor-ends.json", url("ends"))
    }

    @Test
    fun submitEnds() {
        val name = "ends"
        val request = SubmitAttributeRequest("2020-03-13")
        val response = rest.postForEntity(url(name, "/submit"), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        verify(marketplaceManagerApi).updateDiscountAttribute(
            discountId,
            UpdateDiscountAttributeRequest(name, "2020-03-13 00:00:00"),
        )
    }
}
