package com.wutsi.application.membership.onboard.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractEndpointTest
import com.wutsi.application.membership.onboard.dto.SubmitPinRequest
import com.wutsi.application.membership.onboard.entity.OnboardEntity
import com.wutsi.error.ErrorURN
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.membership.manager.dto.CreateMemberRequest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

internal class Onboard05ReviewPageTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val entity = OnboardEntity(
        phoneNumber = PHONE_NUMBER,
        country = "CM",
        language = "en",
        displayName = "Ray Sponsible",
        pin = "123456",
    )

    private val request = SubmitPinRequest(
        pin = entity.pin,
    )

    private fun url(action: String = "") = "http://localhost:$port/onboard/pages/review$action"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(entity).whenever(cache).get(DEVICE_ID, OnboardEntity::class.java)
    }

    @Test
    fun index() {
        assertEndpointEquals("/membership/onboard/pages/review.json", url())
    }

    @Test
    fun submit() {
        // GIVEN
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Page, action.type)
        assertEquals("page:/6", action.url)

        verify(membershipManagerApi).createMember(
            CreateMemberRequest(
                phoneNumber = entity.phoneNumber,
                displayName = entity.displayName,
                country = entity.country,
                pin = entity.pin,
            ),
        )

        verify(cache, never()).put(any(), any())
    }

    @Test
    fun phoneAlreadyAssigned() {
        // GIVEN
        val ex = createConflictException(ErrorURN.PHONE_NUMBER_ALREADY_ASSIGNED.urn)
        doThrow(ex).whenever(membershipManagerApi).createMember(any())

        // WHEN
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Page, action.type)
        assertEquals("page:/6", action.url)
    }
}
