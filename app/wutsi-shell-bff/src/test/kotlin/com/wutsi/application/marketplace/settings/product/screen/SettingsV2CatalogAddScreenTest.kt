package com.wutsi.application.marketplace.settings.product.screen

import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class SettingsV2CatalogAddScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port${Page.getSettingsProductAddUrl()}"

    @Test
    fun index() = assertEndpointEquals("/marketplace/settings/product/screens/product-add.json", url())
}
