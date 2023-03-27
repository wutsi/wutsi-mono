package com.wutsi.application.membership.settings.security.page.passcode

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import com.wutsi.application.membership.settings.security.dto.SubmitPasscodeRequest
import com.wutsi.application.membership.settings.security.entity.PasscodeEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.security.manager.dto.UpdatePasswordRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

internal class Passcode01ConfirmPageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    val request = SubmitPasscodeRequest(
        pin = "123456",
    )

    private fun url(action: String = "") =
        "http://localhost:$port/${Page.getSecurityUrl()}/passcode/pages/confirm$action"

    @Test
    fun index() = assertEndpointEquals("/membership/settings/security/pages/confirm.json", url())

    @Test
    fun submit() {
        // GIVEN
        doReturn(PasscodeEntity(pin = request.pin)).whenever(cache).get(any(), any<Class<*>>())

        // WHEN
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(securityManagerApi).updatePassword(
            UpdatePasswordRequest(
                value = request.pin,
            ),
        )

        val action = response.body!!
        assertEquals(ActionType.Page, action.type)
        assertEquals("page:/2", action.url)
    }

    @Test
    fun passwordMismatch() {
        // GIVEN
        doReturn(PasscodeEntity(pin = "0000000")).whenever(cache).get(any(), any<Class<*>>())

        // WHEN
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Prompt, action.type)
        kotlin.test.assertNotNull(action.prompt)
        assertEquals(DialogType.Error.name, action.prompt?.attributes?.get("type"))
        assertEquals(getText("prompt.error.pin-mismatch"), action.prompt?.attributes?.get("message"))
    }
}
