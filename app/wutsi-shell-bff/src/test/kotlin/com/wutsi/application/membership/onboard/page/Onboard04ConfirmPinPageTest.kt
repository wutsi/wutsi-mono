package com.wutsi.application.membership.onboard.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractEndpointTest
import com.wutsi.application.membership.onboard.dto.SubmitPinRequest
import com.wutsi.application.membership.onboard.entity.OnboardEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.DialogType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertNotNull

internal class Onboard04ConfirmPinPageTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val entity = OnboardEntity(
        phoneNumber = PHONE_NUMBER,
        country = "CM",
        language = "fr",
        displayName = "Ray Sponsible",
        pin = "123456",
    )

    private val request = SubmitPinRequest(
        pin = entity.pin,
    )

    private fun url(action: String = "") = "http://localhost:$port/onboard/pages/confirm-pin$action"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(entity).whenever(cache).get(DEVICE_ID, OnboardEntity::class.java)
    }

    @Test
    fun index() {
        assertEndpointEquals("/membership/onboard/pages/confirm-pin.json", url())
    }

    @Test
    fun submit() {
        // GIVEN
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Page, action.type)
        assertEquals("page:/5", action.url)

        verify(cache, never()).put(any(), any())
    }

    @Test
    fun invalidPin() {
        // GIVEN
        var request = SubmitPinRequest(
            pin = "xxxx",
        )
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Prompt, action.type)
        assertNotNull(action.prompt)
        assertEquals(DialogType.Error.name, action.prompt?.attributes?.get("type"))
        assertEquals(getText("message.error.pin-mismatch"), action.prompt?.attributes?.get("message"))

        verify(cache, never()).put(any(), any())
    }
}
