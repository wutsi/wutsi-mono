package com.wutsi.application.membership.settings.about.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.application.service.EnvironmentDetector
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class AboutScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var environmentDetector: EnvironmentDetector

    private fun url(action: String = "", qs: String = "") = "http://localhost:$port${Page.getAboutUrl()}$action?$qs"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(false).whenever(environmentDetector).prod()

        // Fix device-id and trace-id
        doReturn("device-id").whenever(tracingContext).deviceId()
        doReturn("trace-id").whenever(tracingContext).traceId()
    }

    @Test
    fun index() = assertEndpointEquals("/membership/settings/about/screens/index.json", url())

    @Test
    fun superUser() {
        val member = Fixtures.createMember(superUser = true)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(any())

        assertEndpointEquals("/membership/settings/about/screens/super-user.json", url())
    }

    @Test
    fun switchEnvironment() {
        // WHEN
        val response = rest.postForEntity(url("/switch-environment", "env=PROD"), null, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(securityManagerApi).logout()

        assertEquals("PROD", response.headers["x-environment"]?.get(0))

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertTrue(action.url.startsWith("http://localhost:0${Page.getLoginUrl()}"))
    }
}
