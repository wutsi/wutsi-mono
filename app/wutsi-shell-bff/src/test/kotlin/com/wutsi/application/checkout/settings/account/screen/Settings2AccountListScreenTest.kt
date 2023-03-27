package com.wutsi.application.checkout.settings.account.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.checkout.manager.dto.SearchPaymentMethodResponse
import com.wutsi.enums.PaymentMethodType
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class Settings2AccountListScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port${Page.getSettingsAccountListUrl()}"

    @Test
    fun index() {
        // GIVEN
        val paymentMethods = listOf(
            Fixtures.createPaymentMethodSummary("1111", PaymentMethodType.MOBILE_MONEY, "+237670000010", "MTN"),
            Fixtures.createPaymentMethodSummary("2222", PaymentMethodType.MOBILE_MONEY, "+237690000010", "Orange"),
        )
        doReturn(SearchPaymentMethodResponse(paymentMethods)).whenever(checkoutManagerApi).searchPaymentMethod(any())

        // WHEN/THEN
        assertEndpointEquals("/checkout/settings/account/screens/account-list.json", url())
    }
}
