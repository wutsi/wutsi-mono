package com.wutsi.application.checkout.settings.account.screen

import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class Settings2AccountAddMobileScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port${Page.getSettingsAccountUrl()}/add/mobile"

    @Test
    fun index() {
        assertEndpointEquals("/checkout/settings/account/screens/add-mobile.json", url())
    }
}
