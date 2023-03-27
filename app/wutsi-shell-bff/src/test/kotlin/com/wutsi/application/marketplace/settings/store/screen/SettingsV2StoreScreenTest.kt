package com.wutsi.application.marketplace.settings.store.screen

import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class SettingsV2StoreScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port${Page.getSettingsStoreUrl()}"

    @Test
    fun index() = assertEndpointEquals("/marketplace/settings/store/screens/index.json", url())
}
