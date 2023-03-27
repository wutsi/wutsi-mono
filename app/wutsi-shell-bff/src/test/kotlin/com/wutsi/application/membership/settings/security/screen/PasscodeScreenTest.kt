package com.wutsi.application.membership.settings.security.screen

import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class PasscodeScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url(action: String = "") = "http://localhost:$port${Page.getSecurityUrl()}/passcode$action"

    @Test
    fun index() = assertEndpointEquals("/membership/settings/security/screens/passcode.json", url())
}
