package com.wutsi.application.membership.settings.security.screen

import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class SecurityScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port${Page.getSecurityUrl()}"

    @Test
    fun index() = assertEndpointEquals("/membership/settings/security/screens/index.json", url())
}
