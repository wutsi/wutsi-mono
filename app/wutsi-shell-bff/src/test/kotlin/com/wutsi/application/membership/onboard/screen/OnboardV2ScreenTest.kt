package com.wutsi.application.membership.onboard.screen

import com.wutsi.application.AbstractEndpointTest
import com.wutsi.application.Page
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class OnboardV2ScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port${Page.getOnboardUrl()}"

    @Test
    fun index() = assertEndpointEquals("/membership/onboard/screens/index.json", url())
}
