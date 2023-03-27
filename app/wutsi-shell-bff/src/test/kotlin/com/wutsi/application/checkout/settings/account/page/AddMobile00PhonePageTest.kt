package com.wutsi.application.checkout.settings.account.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.application.checkout.settings.account.dto.SubmitPhoneRequest
import com.wutsi.application.checkout.settings.account.entity.AccountEntity
import com.wutsi.checkout.manager.dto.SearchPaymentProviderResponse
import com.wutsi.enums.PaymentMethodType
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.security.manager.dto.CreateOTPRequest
import com.wutsi.security.manager.dto.CreateOTPResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.util.UUID

internal class AddMobile00PhonePageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url(action: String = "") =
        "http://localhost:$port${Page.getSettingsAccountUrl()}/add/mobile/pages/phone$action"

    @Test
    fun index() {
        val providers = listOf(
            Fixtures.createPaymentProviderSummary("MTN"),
            Fixtures.createPaymentProviderSummary("Orange"),
        )
        doReturn(SearchPaymentProviderResponse(providers)).whenever(checkoutManagerApi)
            .searchPaymentProvider(any())

        assertEndpointEquals("/checkout/settings/account/pages/add-mobile-phone.json", url())
    }

    @Test
    fun submit() {
        // GIVEN
        val provider = Fixtures.createPaymentProviderSummary()
        doReturn(SearchPaymentProviderResponse(listOf(provider))).whenever(checkoutManagerApi)
            .searchPaymentProvider(any())

        val token = UUID.randomUUID().toString()
        doReturn(CreateOTPResponse(token)).whenever(securityManagerApi).createOtp(any())

        // WHEN
        val request = SubmitPhoneRequest(phoneNumber = "+237670000010")
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals("page:/1", action.url)
        assertEquals(ActionType.Page, action.type)

        verify(securityManagerApi).createOtp(
            CreateOTPRequest(
                address = request.phoneNumber,
                type = MessagingType.SMS.name,
            ),
        )

        verify(cache).put(
            DEVICE_ID,
            AccountEntity(
                number = request.phoneNumber,
                ownerName = member.displayName,
                providerId = provider.id,
                type = PaymentMethodType.MOBILE_MONEY.name,
                otpToken = token,
            ),
        )
    }

    @Test
    fun invalidPhone() {
        // GIVEN
        doReturn(SearchPaymentProviderResponse()).whenever(checkoutManagerApi)
            .searchPaymentProvider(any())

        // WHEN
        val request = SubmitPhoneRequest(phoneNumber = "+237670000010")
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Prompt, action.type)
        assertEquals(getText("prompt.error.phone-not-valid-mobile-money"), action.prompt?.attributes?.get("message"))
    }
}
