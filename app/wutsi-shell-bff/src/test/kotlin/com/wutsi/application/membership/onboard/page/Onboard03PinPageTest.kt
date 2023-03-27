package com.wutsi.application.membership.onboard.page

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractEndpointTest
import com.wutsi.application.membership.onboard.dto.SubmitPinRequest
import com.wutsi.application.membership.onboard.entity.OnboardEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class Onboard03PinPageTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val entity = OnboardEntity(
        phoneNumber = PHONE_NUMBER,
        country = "CM",
        language = "fr",
        displayName = "Ray Sponsible",
    )

    private val request = SubmitPinRequest(
        pin = "123456",
    )

    private fun url(action: String = "") = "http://localhost:$port/onboard/pages/pin$action"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(entity).whenever(cache).get(DEVICE_ID, OnboardEntity::class.java)
    }

    @Test
    fun index() {
        assertEndpointEquals("/membership/onboard/pages/pin.json", url())
    }

    @Test
    fun submit() {
        // GIVEN
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val action = response.body!!
        assertEquals(ActionType.Page, action.type)
        assertEquals("page:/4", action.url)

        val obj = argumentCaptor<OnboardEntity>()
        verify(cache).put(eq(DEVICE_ID), obj.capture())
        assertEquals(entity.phoneNumber, obj.firstValue.phoneNumber)
        assertEquals(entity.country, obj.firstValue.country)
        assertEquals(entity.language, obj.firstValue.language)
        assertEquals(entity.displayName, obj.firstValue.displayName)
        assertEquals(request.pin, obj.firstValue.pin)
    }
}
