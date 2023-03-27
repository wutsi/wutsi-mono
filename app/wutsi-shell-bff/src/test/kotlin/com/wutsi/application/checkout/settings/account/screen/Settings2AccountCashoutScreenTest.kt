package com.wutsi.application.checkout.settings.account.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.application.checkout.settings.account.dto.SubmitCashoutRequest
import com.wutsi.checkout.manager.dto.CreateCashoutRequest
import com.wutsi.checkout.manager.dto.GetBusinessResponse
import com.wutsi.checkout.manager.dto.SearchPaymentMethodResponse
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class Settings2AccountCashoutScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url(action: String = "") = "http://localhost:$port${Page.getSettingsAccountCashoutUrl()}$action"

    @Test
    fun index() {
        // GIVEN
        val paymentMethods = listOf(
            Fixtures.createPaymentMethodSummary("11", number = "+237670000001"),
            Fixtures.createPaymentMethodSummary("22", number = "+237690000001"),
        )
        doReturn(SearchPaymentMethodResponse(paymentMethods)).whenever(checkoutManagerApi).searchPaymentMethod(any())

        val member = Fixtures.createMember(id = MEMBER_ID, businessId = 111, business = true)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(any())

        val business = Fixtures.createBusiness(id = 111, accountId = MEMBER_ID)
        doReturn(GetBusinessResponse(business)).whenever(checkoutManagerApi).getBusiness(any())

        // THEN
        assertEndpointEquals("/checkout/settings/account/screens/cashout.json", url())
    }

    @Test
    fun submit() {
        // WHEN
        val request = SubmitCashoutRequest(
            amount = 3000,
            token = "111",
        )
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals("route:/..", action.url)
        assertEquals(ActionType.Route, action.type)

        val req = argumentCaptor<CreateCashoutRequest>()
        verify(checkoutManagerApi).createCashout(req.capture())
        assertEquals(request.amount, req.firstValue.amount)
        assertEquals(request.token, req.firstValue.paymentMethodToken)
        assertNotNull(req.firstValue.idempotencyKey)
    }
}
