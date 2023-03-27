package com.wutsi.application.marketplace.settings.product.screen

import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class SettingsV2ProductCategoryScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port${Page.getSettingsProductCategoryUrl()}?id=11"

    @Test
    fun index() = assertEndpointEquals("/marketplace/settings/product/screens/product-category.json", url())
}
