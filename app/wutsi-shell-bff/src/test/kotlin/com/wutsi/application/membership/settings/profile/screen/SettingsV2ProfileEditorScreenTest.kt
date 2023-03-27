package com.wutsi.application.membership.settings.profile.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.application.membership.settings.profile.dto.SubmitProfileAttributeRequest
import com.wutsi.application.membership.settings.profile.entity.EmailEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.membership.manager.dto.GetMemberResponse
import com.wutsi.membership.manager.dto.SearchPlaceResponse
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.security.manager.dto.CreateOTPResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.util.UUID

internal class SettingsV2ProfileEditorScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url(name: String, action: String = "") =
        "http://localhost:$port${Page.getSettingsUrl()}/profile/editor$action?name=$name"

    @Test
    fun `display-name`() =
        assertEndpointEquals("/membership/settings/profile/screens/editor-display-name.json", url("display-name"))

    @Test
    fun `business-name`() {
        val member = Fixtures.createMember(business = true)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(any())

        assertEndpointEquals("/membership/settings/profile/screens/editor-business-name.json", url("display-name"))
    }

    @Test
    fun `email`() =
        assertEndpointEquals("/membership/settings/profile/screens/editor-email.json", url("email"))

    @Test
    fun `language`() =
        assertEndpointEquals("/membership/settings/profile/screens/editor-language.json", url("language"))

    @Test
    fun `city`() {
        val places = listOf(
            Fixtures.createPlaceSummary(1, "Yaounde"),
            Fixtures.createPlaceSummary(2, "Douala"),
            Fixtures.createPlaceSummary(3, "Bafoussam"),
        )
        doReturn(SearchPlaceResponse(places)).whenever(membershipManagerApi).searchPlace(any())

        assertEndpointEquals("/membership/settings/profile/screens/editor-city.json", url("city-id"))
    }

    @Test
    fun submit() {
        // WHEN
        val request = SubmitProfileAttributeRequest(
            value = "Foo",
        )
        val response = rest.postForEntity(url("display-name", "/submit"), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/..", action.url)

        verify(membershipManagerApi).updateMemberAttribute(
            request = UpdateMemberAttributeRequest(
                name = "display-name",
                value = request.value,
            ),
        )

        assertNull(response.headers["x-language"])
    }

    @Test
    fun submitLanguage() {
        // WHEN
        val request = SubmitProfileAttributeRequest(
            value = "fr",
        )
        val response = rest.postForEntity(url("language", "/submit"), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/..", action.url)

        verify(membershipManagerApi).updateMemberAttribute(
            request = UpdateMemberAttributeRequest(
                name = "language",
                value = request.value,
            ),
        )

        assertEquals(listOf(request.value), response.headers["x-language"])
    }

    @Test
    fun submitEmail() {
        // GIVEN
        val token = UUID.randomUUID().toString()
        doReturn(CreateOTPResponse(token)).whenever(securityManagerApi).createOtp(any())

        // WHEN
        val request = SubmitProfileAttributeRequest(
            value = "yo@gmail.com",
        )
        val response = rest.postForEntity(url("email", "/submit"), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0${Page.getSettingsUrl()}/profile/email/verification", action.url)
        assertEquals(true, action.replacement)

        verify(membershipManagerApi, never()).updateMemberAttribute(any())

        verify(cache).put(
            DEVICE_ID,
            EmailEntity(
                value = request.value,
                token = token,
            ),
        )

        assertNull(response.headers["x-language"])
    }

    @Test
    fun submitSameEmail() {
        // GIVEN
        val token = UUID.randomUUID().toString()
        doReturn(CreateOTPResponse(token)).whenever(securityManagerApi).createOtp(any())

        // WHEN
        val request = SubmitProfileAttributeRequest(
            value = member.email ?: "",
        )
        val response = rest.postForEntity(url("email", "/submit"), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/..", action.url)

        verify(membershipManagerApi, never()).updateMemberAttribute(any())
        verify(cache, never()).put(any(), any())

        assertNull(response.headers["x-language"])
    }
}
