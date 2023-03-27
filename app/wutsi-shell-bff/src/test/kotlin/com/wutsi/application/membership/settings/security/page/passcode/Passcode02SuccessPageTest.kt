package com.wutsi.application.membership.settings.security.page.passcode

import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class Passcode02SuccessPageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() =
        "http://localhost:$port/${Page.getSecurityUrl()}/passcode/pages/success"

    @Test
    fun index() = assertEndpointEquals("/membership/settings/security/pages/success.json", url())
}
