package com.wutsi.application.membership.login.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.application.membership.login.dto.SubmitPasscodeRequest
import com.wutsi.application.service.EnvironmentDetector
import com.wutsi.enums.LoginType
import com.wutsi.error.ErrorURN
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.membership.manager.dto.SearchMemberResponse
import com.wutsi.security.manager.dto.LoginRequest
import com.wutsi.security.manager.dto.LoginResponse
import com.wutsi.security.manager.dto.VerifyPasswordRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.net.URLEncoder
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class LoginV2ScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val phoneNumber = "+237670000010"

    private val accessToken = "0000-230932-1adf"

    private val request = SubmitPasscodeRequest(pin = "123")

    @MockBean
    private lateinit var env: EnvironmentDetector

    private fun url(path: String = "", qs: String = "") =
        "http://localhost:$port${Page.getLoginUrl()}$path?phone=$phoneNumber&$qs"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(false).whenever(env).test()

        val member = Fixtures.createMemberSummary()
        doReturn(SearchMemberResponse(listOf(member))).whenever(membershipManagerApi).searchMember(any())
    }

    @Test
    fun login() = assertEndpointEquals("/membership/login/screens/login.json", url())

    @Test
    fun hideBackButton() =
        assertEndpointEquals("/membership/login/screens/hide-back-button.json", url() + "&hide-back-button=true")

    @Test
    fun customLoginScreen() {
        assertEndpointEquals(
            "/membership/login/screens/login-custom.json",
            url() + "&title=Foo&sub-title=Yo+Man&icon=i_c_o_n&return-to-route=false&return-url=https://www.google.ca&dark-mode=true",
        )
    }

    @Test
    fun authenticate() {
        // GIVEN
        doReturn(LoginResponse(accessToken)).whenever(securityManagerApi).login(any())

        // WHEN
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        assertEquals(listOf(accessToken), response.headers["x-access-token"])

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0${Page.getHomeUrl()}", action.url)
        assertEquals(true, action.replacement)

        com.nhaarman.mockitokotlin2.verify(securityManagerApi).login(
            LoginRequest(
                type = LoginType.PASSWORD.name,
                username = phoneNumber,
                password = request.pin,
            ),
        )
    }

    @Test
    fun authenticateAndRedirect() {
        // GIVEN
        doReturn(LoginResponse(accessToken)).whenever(securityManagerApi).login(any())

        // WHEN
        val redirectUrl = "https://www.google.ca"
        val qs = "auth=true&return-url=" + URLEncoder.encode(redirectUrl, "utf-8")
        val response = rest.postForEntity(url("/submit", qs), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        assertEquals(listOf(accessToken), response.headers["x-access-token"])

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals(redirectUrl, action.url)
        assertEquals(true, action.replacement)

        com.nhaarman.mockitokotlin2.verify(securityManagerApi).login(
            LoginRequest(
                type = LoginType.PASSWORD.name,
                username = phoneNumber,
                password = request.pin,
            ),
        )
    }

    @Test
    fun authenticateFailure() {
        // GIVEN
        val ex = createConflictException(errorCode = ErrorURN.MEMBER_NOT_ACTIVE.urn)
        doThrow(ex).whenever(securityManagerApi).login(any())

        // WHEN
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        assertNull(response.headers["x-access-token"])

        val action = response.body!!
        assertEquals(ActionType.Prompt, action.type)
        assertEquals(DialogType.Error.name, action.prompt?.attributes?.get("type"))
        assertEquals(getText("message.error.login-failed"), action.prompt?.attributes?.get("message"))
    }

    @Test
    fun verify() {
        // GIVEN
        doReturn(LoginResponse(accessToken)).whenever(securityManagerApi).login(any())

        // WHEN
        val response = rest.postForEntity(url("/submit", "auth=false"), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        assertNull(response.headers["x-access-token"])

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0${Page.getHomeUrl()}", action.url)
        assertEquals(true, action.replacement)

        com.nhaarman.mockitokotlin2.verify(securityManagerApi).verifyPassword(
            VerifyPasswordRequest(
                value = request.pin,
            ),
        )
    }

    @Test
    fun verifyAndRedirect() {
        // GIVEN
        doReturn(LoginResponse(accessToken)).whenever(securityManagerApi).login(any())

        // WHEN
        val redirectUrl = "https://www.google.ca"
        val qs = "auth=false&return-to-route=false&return-url=" + URLEncoder.encode(redirectUrl, "utf-8")
        val response = rest.postForEntity(url("/submit", qs), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        assertNull(response.headers["x-access-token"])

        val action = response.body!!
        assertEquals(ActionType.Command, action.type)
        assertEquals(redirectUrl, action.url)
        assertNull(action.replacement)

        com.nhaarman.mockitokotlin2.verify(securityManagerApi).verifyPassword(
            VerifyPasswordRequest(
                value = request.pin,
            ),
        )
    }

    @Test
    fun testEnvironemnt() {
        doReturn(true).whenever(env).test()
        assertEndpointEquals("/membership/login/screens/login-test-env.json", url())
    }
}
