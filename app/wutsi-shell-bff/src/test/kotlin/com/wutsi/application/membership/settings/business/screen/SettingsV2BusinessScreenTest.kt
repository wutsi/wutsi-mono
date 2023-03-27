package com.wutsi.application.membership.settings.business.screen

import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class SettingsV2BusinessScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port${Page.getSettingsBusinessUrl()}"

    @Test
    fun index() = assertEndpointEquals("/membership/settings/business/screens/index.json", url())
}
