package com.wutsi.application.membership.settings.security.page.passcode

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import com.wutsi.application.membership.settings.security.dto.SubmitPasscodeRequest
import com.wutsi.application.membership.settings.security.entity.PasscodeEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class Passcode00PINPageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    val request = SubmitPasscodeRequest(
        pin = "123456",
    )

    private fun url(action: String = "") = "http://localhost:$port/${Page.getSecurityUrl()}/passcode/pages/pin$action"

    @Test
    fun index() = assertEndpointEquals("/membership/settings/security/pages/pin.json", url())

    @Test
    fun submit() {
        // WHEN
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val action = response.body!!
        assertEquals(ActionType.Page, action.type)
        assertEquals("page:/1", action.url)

        val entity = argumentCaptor<PasscodeEntity>()
        verify(cache).put(eq(DEVICE_ID), entity.capture())
        assertEquals(request.pin, entity.firstValue.pin)
    }
}
