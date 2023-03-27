package com.wutsi.application.checkout.settings.account.page

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import com.wutsi.application.checkout.settings.account.dto.SubmitOTPRequest
import com.wutsi.application.checkout.settings.account.entity.AccountEntity
import com.wutsi.checkout.manager.dto.AddPaymentMethodRequest
import com.wutsi.enums.PaymentMethodType
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.security.manager.dto.VerifyOTPRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

internal class AddMobile01VerificationPageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val entity = AccountEntity(
        number = "+237670000010",
        type = PaymentMethodType.MOBILE_MONEY.name,
        ownerName = "Ray Sponsible",
        otpToken = "1111",
        providerId = 1L,
    )

    private fun url(action: String = "") =
        "http://localhost:$port${Page.getSettingsAccountUrl()}/add/mobile/pages/verification$action"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(entity).whenever(cache).get(DEVICE_ID, AccountEntity::class.java)
    }

    @Test
    fun index() {
        assertEndpointEquals("/checkout/settings/account/pages/add-mobile-verification.json", url())
    }

    @Test
    fun submit() {
        // GIVEN

        // WHEN
        val request = SubmitOTPRequest(code = "111111")
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals("page:/2", action.url)
        assertEquals(ActionType.Page, action.type)

        verify(securityManagerApi).verifyOtp(
            token = entity.otpToken,
            request = VerifyOTPRequest(code = request.code),
        )

        verify(checkoutManagerApi).addPaymentMethod(
            request = AddPaymentMethodRequest(
                providerId = entity.providerId,
                type = PaymentMethodType.MOBILE_MONEY.name,
                number = entity.number,
                ownerName = entity.ownerName,
                country = "CM",
            ),
        )
    }
}
