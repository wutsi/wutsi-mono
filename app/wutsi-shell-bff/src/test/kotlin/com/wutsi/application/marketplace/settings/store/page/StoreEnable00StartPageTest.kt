package com.wutsi.application.marketplace.settings.store.page

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

internal class StoreEnable00StartPageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url(action: String = "") =
        "http://localhost:$port${Page.getSettingsStoreActivateUrl()}/pages/start$action"

    @Test
    fun index() = assertEndpointEquals("/marketplace/settings/store/pages/start.json", url())

    @Test
    fun submit() {
        // WHEN
        val response = rest.postForEntity(url("/submit"), null, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Page, action.type)
        assertEquals("page:/1", action.url)

        verify(marketplaceManagerApi).createStore()
    }
}
