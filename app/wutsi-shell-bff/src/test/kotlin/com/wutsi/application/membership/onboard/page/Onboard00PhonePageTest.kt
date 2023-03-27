package com.wutsi.application.membership.onboard.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractEndpointTest
import com.wutsi.application.membership.onboard.dto.SubmitPhoneRequest
import com.wutsi.application.membership.onboard.entity.OnboardEntity
import com.wutsi.application.service.EnvironmentDetector
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.membership.manager.dto.MemberSummary
import com.wutsi.membership.manager.dto.SearchMemberResponse
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.security.manager.dto.CreateOTPRequest
import com.wutsi.security.manager.dto.CreateOTPResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

internal class Onboard00PhonePageTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var environmentDetector: EnvironmentDetector

    private val request = SubmitPhoneRequest(
        phoneNumber = PHONE_NUMBER,
    )

    @BeforeTest
    override fun setUp() {
        super.setUp()

        doReturn(false).whenever(environmentDetector).test()
    }

    private fun url(action: String = "") = "http://localhost:$port/onboard/pages/phone$action"

    @Test
    fun index() = assertEndpointEquals("/membership/onboard/pages/phone.json", url())

    @Test
    fun submit() {
        // GIVEN
        doReturn(SearchMemberResponse()).whenever(membershipManagerApi).searchMember(any())

        val token = "55555"
        doReturn(CreateOTPResponse(token = token)).whenever(securityManagerApi).createOtp(any())

        // GIVEN
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val action = response.body!!
        assertEquals(ActionType.Page, action.type)
        assertEquals("page:/1", action.url)

        val entity = argumentCaptor<OnboardEntity>()
        verify(cache).put(eq(DEVICE_ID), entity.capture())
        assertEquals(request.phoneNumber, entity.firstValue.phoneNumber)
        assertEquals("CM", entity.firstValue.country)
        assertEquals("en", entity.firstValue.language)
        assertEquals(token, entity.firstValue.otpToken)

        val req = argumentCaptor<CreateOTPRequest>()
        verify(securityManagerApi).createOtp(req.capture())
        assertEquals(request.phoneNumber, req.firstValue.address)
        assertEquals(MessagingType.SMS.name, req.firstValue.type)
    }

    @Test
    fun submitMemberExist() {
        // GIVEN
        val members = listOf(MemberSummary())
        doReturn(SearchMemberResponse(members)).whenever(membershipManagerApi).searchMember(any())

        // GIVEN
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals(
            "http://localhost:0/login/2?title=You+have+a+wallet%21&sub-title=Enter+your+Passcode&phone=%2B237670000010&return-to-route=true&hide-change-account-button=true",
            action.url,
        )

        verify(cache, never()).put(any(), any())
    }
}
