package com.wutsi.application.membership.settings.home.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

internal class SettingsV2ScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url(action: String = "") = "http://localhost:$port${Page.getSettingsUrl()}$action"

    @Test
    fun personal() = assertEndpointEquals("/membership/settings/home/screens/personal.json", url())

    @Test
    fun business() {
        val member = Fixtures.createMember(id = MEMBER_ID, business = true)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(any())

        assertEndpointEquals("/membership/settings/home/screens/business.json", url())
    }

    @Test
    fun storeEnabled() {
        val member = Fixtures.createMember(id = MEMBER_ID, business = true, storeId = 111L)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(any())

        assertEndpointEquals("/membership/settings/home/screens/store.json", url())
    }

    @Test
    fun logout() {
        // GIVEN
        val response = rest.postForEntity(url("/logout"), null, Action::class.java)

        // WHEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(securityManagerApi).logout()

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals(
            "http://localhost:0${Page.getLoginUrl()}?title=&sub-title=Enter+your+Passcode&phone=%2B237670000010&return-to-route=true&hide-change-account-button=false",
            action.url,
        )
        assertEquals(true, action.replacement)
    }
}
