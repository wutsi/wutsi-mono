package com.wutsi.application.marketplace.settings.fundraising.screen

import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class SettingsV2FundraisingEnableScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port${Page.getSettingsFundraisingActivateUrl()}"

    @Test
    fun index() = assertEndpointEquals("/marketplace/settings/fundraising/screens/enable.json", url())
}
