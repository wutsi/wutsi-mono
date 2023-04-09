package com.wutsi.application.marketplace.settings.fundraising.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.application.marketplace.settings.product.dto.SubmitAttributeRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.marketplace.manager.dto.GetFundraisingResponse
import com.wutsi.marketplace.manager.dto.UpdateFundraisingAttributeRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

internal class SettingsV2FundraisingEditorScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val fundraising = Fixtures.createFundraising(id = 100)

    private fun url(name: String, action: String = "") =
        "http://localhost:$port${Page.getSettingsFundraisingEditorUrl()}$action?id=${fundraising.id}&name=$name"

    @BeforeEach
    override fun setUp() {
        super.setUp()
        doReturn(GetFundraisingResponse(fundraising)).whenever(marketplaceManagerApi).getFundraising(any())
    }

    @Test
    fun amount() =
        assertEndpointEquals("/marketplace/settings/fundraising/screens/editor-amount.json", url("amount"))

    @Test
    fun description() =
        assertEndpointEquals("/marketplace/settings/fundraising/screens/editor-description.json", url("description"))

    @Test
    fun video() =
        assertEndpointEquals("/marketplace/settings/fundraising/screens/editor-video-url.json", url("video-url"))

    @Test
    fun submit() {
        // WHEN
        val request = SubmitAttributeRequest("bar")
        val response = rest.postForEntity(url("title", "/submit"), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/..", action.url)

        verify(marketplaceManagerApi).updateFundraisingAttribute(
            100,
            UpdateFundraisingAttributeRequest("title", request.value),
        )
    }
}
