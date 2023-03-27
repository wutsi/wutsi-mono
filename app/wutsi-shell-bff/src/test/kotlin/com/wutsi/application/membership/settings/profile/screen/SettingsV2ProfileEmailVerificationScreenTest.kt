package com.wutsi.application.membership.settings.profile.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import com.wutsi.application.membership.settings.profile.dto.SubmitOTPRequest
import com.wutsi.application.membership.settings.profile.entity.EmailEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.security.manager.dto.CreateOTPResponse
import com.wutsi.security.manager.dto.VerifyOTPRequest
import com.wutsi.security.manager.error.ErrorURN
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class SettingsV2ProfileEmailVerificationScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    val entity = EmailEntity(
        value = "yo@man.com",
        token = "043049309",
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(entity).whenever(cache).get(DEVICE_ID, EmailEntity::class.java)
    }

    @Test
    fun index() = assertEndpointEquals("/membership/settings/profile/screens/email-verification.json", url())

    @Test
    fun submit() {
        // GIVEN

        // WHEN
        val request = SubmitOTPRequest("3043909")
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/..", action.url)

        verify(securityManagerApi).verifyOtp(
            token = entity.token,
            request = VerifyOTPRequest(
                code = request.code,
            ),
        )
        verify(membershipManagerApi).updateMemberAttribute(
            request = UpdateMemberAttributeRequest(
                name = "email",
                value = entity.value,
            ),
        )
    }

    @Test
    fun submitInvalidOTP() {
        // GIVEN
        val ex = createConflictException(errorCode = ErrorURN.OTP_NOT_VALID.urn)
        doThrow(ex).whenever(securityManagerApi).verifyOtp(any(), any())

        // WHEN
        val request = SubmitOTPRequest("3043909")
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        val action = response.body!!
        assertEquals(ActionType.Prompt, action.type)
        assertNotNull(action.prompt)
        assertEquals(DialogType.Error.name, action.prompt?.attributes?.get("type"))
        assertEquals(
            getText("prompt.error.otp-mismatch"),
            action.prompt?.attributes?.get("message"),
        )

        verify(membershipManagerApi, never()).updateMemberAttribute(any())
    }

    @Test
    fun submitExpiredOTP() {
        // GIVEN
        val ex = createConflictException(errorCode = ErrorURN.OTP_EXPIRED.urn)
        doThrow(ex).whenever(securityManagerApi).verifyOtp(any(), any())

        // WHEN
        val request = SubmitOTPRequest("3043909")
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        val action = response.body!!
        assertEquals(ActionType.Prompt, action.type)
        assertNotNull(action.prompt)
        assertEquals(DialogType.Error.name, action.prompt?.attributes?.get("type"))
        assertEquals(
            getText("prompt.error.otp-expired"),
            action.prompt?.attributes?.get("message"),
        )

        verify(membershipManagerApi, never()).updateMemberAttribute(any())
    }

    @Test
    fun resend() {
        // GIVEN
        val token = UUID.randomUUID().toString()
        doReturn(CreateOTPResponse(token)).whenever(securityManagerApi).createOtp(any())

        // WHEN
        val response = rest.postForEntity(url("/resend"), null, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(cache).put(
            DEVICE_ID,
            EmailEntity(value = entity.value, token = token),
        )
    }

    private fun url(action: String = "") =
        "http://localhost:$port${Page.getSettingsUrl()}/profile/email/verification$action"
}
