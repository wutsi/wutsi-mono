package com.wutsi.application.membership.settings.business.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import com.wutsi.application.membership.settings.business.entity.BusinessEntity
import com.wutsi.application.membership.settings.profile.dto.SubmitOTPRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.security.manager.dto.CreateOTPRequest
import com.wutsi.security.manager.dto.CreateOTPResponse
import com.wutsi.security.manager.dto.VerifyOTPRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import java.util.UUID

internal class Business06EmailVerificationPageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val entity = BusinessEntity(
        displayName = "Maison H",
        categoryId = 22L,
        cityId = 100L,
        whatsapp = true,
        email = "info@maison-h.com",
        otpToken = UUID.randomUUID().toString(),
    )

    private fun url(action: String = "") =
        "http://localhost:$port${Page.getSettingsBusinessUrl()}/pages/email/verification$action"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(entity).whenever(cache).get(DEVICE_ID, BusinessEntity::class.java)
    }

    @Test
    fun index() = assertEndpointEquals("/membership/settings/business/pages/email-verification.json", url())

    @Test
    fun submit() {
        // WHEN
        val request = SubmitOTPRequest("010101")
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val action = response.body!!
        assertEquals(ActionType.Page, action.type)
        assertEquals("page:/${Business06EmailVerificationPage.PAGE_INDEX + 1}", action.url)

        verify(securityManagerApi).verifyOtp(
            entity.otpToken,
            request = VerifyOTPRequest(
                code = request.code,
            ),
        )
    }

    @Test
    fun resend() {
        // GIVEN
        val otpToken = "xxx"
        doReturn(CreateOTPResponse(otpToken)).whenever(securityManagerApi).createOtp(any())

        // WHEN
        val response = rest.postForEntity(url("/resend"), null, Any::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        verify(securityManagerApi).createOtp(
            request = CreateOTPRequest(
                address = entity.email,
                type = MessagingType.EMAIL.name,
            ),
        )

        verify(cache).put(
            DEVICE_ID,
            BusinessEntity(
                displayName = entity.displayName,
                categoryId = entity.categoryId,
                cityId = entity.cityId,
                whatsapp = entity.whatsapp,
                email = entity.email,
                otpToken = otpToken,
            ),
        )
    }
}
