package com.wutsi.application.checkout.settings.account.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.checkout.manager.dto.GetPaymentMethodResponse
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

internal class Settings2AccountScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url(token: String, action: String = "") =
        "http://localhost:$port${Page.getSettingsAccountUrl()}$action?token=$token"

    @Test
    fun index() {
        // GIVEN
        val paymentMethod = Fixtures.createPaymentMethod()
        doReturn(GetPaymentMethodResponse(paymentMethod)).whenever(checkoutManagerApi).getPaymentMethod(any())

        assertEndpointEquals("/checkout/settings/account/screens/account.json", url("1111"))
    }

    @Test
    fun delete() {
        val response = rest.postForEntity(url("1111", "/delete"), null, Action::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals("route:/..", action.url)
        assertEquals(ActionType.Route, action.type)

        verify(checkoutManagerApi).removePaymentMethod("1111")
    }
}
