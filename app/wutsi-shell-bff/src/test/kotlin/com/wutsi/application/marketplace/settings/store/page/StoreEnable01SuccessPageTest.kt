package com.wutsi.application.marketplace.settings.store.page

import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class StoreEnable01SuccessPageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url(action: String = "") =
        "http://localhost:$port${Page.getSettingsStoreActivateUrl()}/pages/success$action"

    @Test
    fun index() = assertEndpointEquals("/marketplace/settings/store/pages/success.json", url())
}
