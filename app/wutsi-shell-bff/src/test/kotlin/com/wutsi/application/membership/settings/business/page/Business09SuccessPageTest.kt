package com.wutsi.application.membership.settings.business.page

import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class Business09SuccessPageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() =
        "http://localhost:$port${Page.getSettingsBusinessUrl()}/pages/success"

    @Test
    fun index() = assertEndpointEquals("/membership/settings/business/pages/success.json", url())
}
