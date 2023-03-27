package com.wutsi.application.marketplace.settings.discount.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import com.wutsi.application.marketplace.settings.discount.dto.SubmitDiscountRequest
import com.wutsi.enums.DiscountType
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.marketplace.manager.dto.CreateDiscountRequest
import com.wutsi.marketplace.manager.dto.CreateDiscountResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.time.OffsetDateTime
import java.time.ZoneOffset

internal class SettingsV2DiscountAddScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url(action: String = "") = "http://localhost:$port${Page.getSettingsDiscountAddUrl()}$action"

    @Test
    fun add() {
        assertEndpointEquals("/marketplace/settings/discount/screens/add.json", url())
    }

    @Test
    fun submit() {
        // GIVEN
        val discountId = 111L
        doReturn(CreateDiscountResponse(discountId)).whenever(marketplaceManagerApi).createDiscount(any())

        // WHEN
        val request = SubmitDiscountRequest(
            name = "FOO",
            starts = "2020-12-22",
            ends = "2020-12-31",
            rate = 50,
        )
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0/settings/2/discounts", action.url)
        assertEquals(mapOf("id" to discountId.toString()), action.parameters)
        assertEquals(true, action.replacement)

        verify(marketplaceManagerApi).createDiscount(
            request = CreateDiscountRequest(
                name = request.name,
                starts = OffsetDateTime.of(2020, 12, 22, 0, 0, 0, 0, ZoneOffset.UTC),
                ends = OffsetDateTime.of(2020, 12, 31, 0, 0, 0, 0, ZoneOffset.UTC),
                rate = request.rate,
                allProducts = false,
                type = DiscountType.SALES.name,
            ),
        )
    }
}
