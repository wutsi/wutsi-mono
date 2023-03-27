package com.wutsi.application.membership.settings.profile.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.application.membership.settings.profile.dto.SubmitProfileAttributeRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.membership.manager.dto.GetMemberResponse
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import kotlin.test.assertEquals

internal class SettingsV2ProfileScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url(action: String = "", qs: String = "") =
        "http://localhost:$port${Page.getSettingsUrl()}/profile$action?$qs"

    @Test
    fun personal() = assertEndpointEquals("/membership/settings/profile/screens/personal.json", url())

    @Test
    fun business() {
        val member = Fixtures.createMember(business = true)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(any())

        assertEndpointEquals("/membership/settings/profile/screens/business.json", url())
    }

    @Test
    fun businessNotSupported() {
        val member = Fixtures.createMember(country = "NZ")
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(any())

        assertEndpointEquals("/membership/settings/profile/screens/business-not-supported.json", url())
    }

    @Test
    fun submit() {
        // WHEN
        val request = SubmitProfileAttributeRequest("true")
        val response = rest.postForEntity(url("/submit", "name=whatsapp"), request, Action::class.java)

        // THEN
        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0${Page.getSettingsProfileUrl()}", action.url)

        verify(membershipManagerApi).updateMemberAttribute(
            request = UpdateMemberAttributeRequest(
                name = "whatsapp",
                value = request.value,
            ),
        )
    }
}
