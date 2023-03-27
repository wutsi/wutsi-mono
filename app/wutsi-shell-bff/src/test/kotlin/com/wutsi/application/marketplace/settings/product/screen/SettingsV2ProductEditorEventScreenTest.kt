package com.wutsi.application.marketplace.settings.product.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.application.marketplace.settings.product.dto.SubmitProductEventRequest
import com.wutsi.enums.MeetingProviderType
import com.wutsi.enums.ProductType
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.marketplace.manager.dto.Event
import com.wutsi.marketplace.manager.dto.GetProductResponse
import com.wutsi.marketplace.manager.dto.SearchMeetingProviderResponse
import com.wutsi.marketplace.manager.dto.UpdateProductEventRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals

internal class SettingsV2ProductEditorEventScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val productId = 111L
    private val meetingProviders = listOf(
        Fixtures.createMeetingProviderType(1000, MeetingProviderType.MEET, "Meet"),
        Fixtures.createMeetingProviderType(1001, MeetingProviderType.ZOOM, "Zoom"),
    )
    val product = Fixtures.createProduct(
        id = productId,
        pictures = Fixtures.createPictureSummaryList(2),
        type = ProductType.EVENT,
    )

    private fun url(action: String = "") =
        "http://localhost:$port${Page.getSettingsProductEditorUrl()}/event$action?id=$productId"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetProductResponse(product)).whenever(marketplaceManagerApi).getProduct(any())
        doReturn(SearchMeetingProviderResponse(meetingProviders)).whenever(marketplaceManagerApi)
            .searchMeetingProvider()
    }

    @Test
    fun createEvent() {
        val product = Fixtures.createProduct(
            id = productId,
            pictures = Fixtures.createPictureSummaryList(2),
            type = ProductType.EVENT,
            event = Event(),
        )
        doReturn(GetProductResponse(product)).whenever(marketplaceManagerApi).getProduct(any())

        assertEndpointEquals("/marketplace/settings/product/screens/editor-create-event.json", url())
    }

    @Test
    fun updateEvent() {
        val product = Fixtures.createProduct(
            id = productId,
            pictures = Fixtures.createPictureSummaryList(2),
            type = ProductType.EVENT,
            event = Fixtures.createEvent(meetingProvider = Fixtures.createMeetingProviderSummary()),
        )
        doReturn(GetProductResponse(product)).whenever(marketplaceManagerApi).getProduct(any())

        assertEndpointEquals("/marketplace/settings/product/screens/editor-edit-event.json", url())
    }

    @Test
    fun submit() {
        val request = SubmitProductEventRequest(
            meetingId = "1234567890",
            meetingPassword = "123456",
            startDate = "2020-12-11",
            startTime = "12:30",
            endTime = "15:00",
            meetingProviderId = 1000,
        )
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/..", action.url)

        verify(marketplaceManagerApi).updateProductEvent(
            request = UpdateProductEventRequest(
                productId = productId,
                online = true,
                meetingProviderId = request.meetingProviderId,
                meetingPassword = request.meetingPassword,
                meetingId = request.meetingId,
                starts = OffsetDateTime.of(2020, 12, 11, 11, 30, 0, 0, ZoneOffset.UTC),
                ends = OffsetDateTime.of(2020, 12, 11, 14, 0, 0, 0, ZoneOffset.UTC),
            ),
        )
    }
}
