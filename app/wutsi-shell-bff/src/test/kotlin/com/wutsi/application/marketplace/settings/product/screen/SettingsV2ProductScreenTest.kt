package com.wutsi.application.marketplace.settings.product.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.checkout.manager.dto.SearchOrderResponse
import com.wutsi.checkout.manager.dto.SearchSalesKpiResponse
import com.wutsi.enums.ProductType
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.marketplace.manager.dto.CreatePictureRequest
import com.wutsi.marketplace.manager.dto.GetProductResponse
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.regulation.RegulationEngine
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.net.URL
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class SettingsV2ProductScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @Autowired
    private lateinit var regulationEngine: RegulationEngine

    @MockBean
    private lateinit var storageService: StorageService

    @MockBean
    private lateinit var clock: Clock

    private val productId = 123L

    private fun url(action: String = "") =
        "http://localhost:$port${Page.getSettingsProductUrl()}$action?id=$productId"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        val orders = listOf(
            Fixtures.createOrderSummary("1"),
            Fixtures.createOrderSummary("2"),
            Fixtures.createOrderSummary("3"),
        )
        doReturn(SearchOrderResponse(orders)).whenever(checkoutManagerApi).searchOrder(any())

        val kpis = listOf(
            Fixtures.createSalesKpiSummary(),
        )
        doReturn(SearchSalesKpiResponse(kpis)).whenever(checkoutManagerApi).searchSalesKpi(any())

        val today = LocalDate.of(2020, 2, 1)
        doReturn(today.atStartOfDay().toInstant(ZoneOffset.UTC)).whenever(clock).instant()
    }

    @Test
    fun draft() {
        val product = Fixtures.createProduct(
            pictures = Fixtures.createPictureSummaryList(2),
            published = false,
        )
        doReturn(GetProductResponse(product)).whenever(marketplaceManagerApi).getProduct(any())

        assertEndpointEquals("/marketplace/settings/product/screens/product-draft.json", url())
    }

    @Test
    fun published() {
        val product = Fixtures.createProduct(
            id = 111,
            pictures = Fixtures.createPictureSummaryList(2),
            published = true,
        )
        doReturn(GetProductResponse(product)).whenever(marketplaceManagerApi).getProduct(any())

        assertEndpointEquals("/marketplace/settings/product/screens/product-published.json", url())
    }

    @Test
    fun event() {
        val product = Fixtures.createProduct(
            id = 111,
            pictures = Fixtures.createPictureSummaryList(2),
            published = true,
            type = ProductType.EVENT,
            event = Fixtures.createEvent(
                meetingProvider = Fixtures.createMeetingProviderSummary(),
            ),
        )
        doReturn(GetProductResponse(product)).whenever(marketplaceManagerApi).getProduct(any())

        assertEndpointEquals("/marketplace/settings/product/screens/product-event.json", url())
    }

    @Test
    fun digitalDownload() {
        val product = Fixtures.createProduct(
            id = 111,
            pictures = Fixtures.createPictureSummaryList(2),
            published = true,
            type = ProductType.DIGITAL_DOWNLOAD,
            files = listOf(
                Fixtures.createFileSummary(1L, name = "1.png"),
                Fixtures.createFileSummary(2L, name = "1.xls"),
                Fixtures.createFileSummary(3L, name = "1.pdf"),
            ),
        )
        doReturn(GetProductResponse(product)).whenever(marketplaceManagerApi).getProduct(any())

        assertEndpointEquals("/marketplace/settings/product/screens/product-digital-download.json", url())
    }

    @Test
    fun indexPictureLimit() {
        val product = Fixtures.createProduct(
            id = 111,
            pictures = Fixtures.createPictureSummaryList(regulationEngine.maxPictures()),
        )
        doReturn(GetProductResponse(product)).whenever(marketplaceManagerApi).getProduct(any())

        assertEndpointEquals("/marketplace/settings/product/screens/product-picture-limit.json", url())
    }

    @Test
    fun upload() {
        // GIVEN
        val filename = "toto.png"
        val fileUrl = URL("http://www.wutsi.com/asset/1/$filename")
        doReturn(fileUrl).whenever(storageService).store(any(), any(), anyOrNull(), anyOrNull(), anyOrNull())

        // WHEN
        uploadFile(url("/upload"), "toto.png")

        // THEN
        val path = argumentCaptor<String>()
        verify(storageService).store(path.capture(), any(), eq("image/png"), anyOrNull(), anyOrNull())
        assertTrue(path.firstValue.startsWith("product/$productId/picture/"))
        assertTrue(path.firstValue.endsWith(filename))

        verify(marketplaceManagerApi).createPicture(
            request = CreatePictureRequest(
                productId = productId,
                url = fileUrl.toString(),
            ),
        )
    }

    @Test
    fun publish() {
        // WHEN
        val response = rest.postForEntity(url("/publish"), null, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0/settings/2/products?id=123", action.url)

        verify(marketplaceManagerApi).publishProduct(productId)
    }

    @Test
    fun unpublish() {
        // WHEN
        val response = rest.postForEntity(url("/unpublish"), null, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0/settings/2/products?id=123", action.url)

        verify(marketplaceManagerApi).unpublishProduct(productId)
    }

    @Test
    fun delete() {
        // WHEN
        val response = rest.postForEntity(url("/delete"), null, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/..", action.url)

        verify(marketplaceManagerApi).deleteProduct(productId)
    }
}
