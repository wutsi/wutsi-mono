package com.wutsi.application.marketplace.settings.product.screen

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
import com.wutsi.marketplace.manager.dto.GetProductResponse
import com.wutsi.marketplace.manager.dto.ProductAttribute
import com.wutsi.marketplace.manager.dto.UpdateProductAttributeListRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

internal class SettingsV2ProductEditorScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val product = Fixtures.createProduct(
        id = 123,
        pictures = Fixtures.createPictureSummaryList(2),
    )

    private fun url(name: String, action: String = "") =
        "http://localhost:$port${Page.getSettingsProductEditorUrl()}$action?id=${product.id}&name=$name"

    @BeforeEach
    override fun setUp() {
        super.setUp()
        doReturn(GetProductResponse(product)).whenever(marketplaceManagerApi).getProduct(any())
    }

    @Test
    fun `title`() =
        assertEndpointEquals("/marketplace/settings/product/screens/editor-title.json", url("title"))

    @Test
    fun `summary`() =
        assertEndpointEquals("/marketplace/settings/product/screens/editor-summary.json", url("summary"))

    @Test
    fun `description`() =
        assertEndpointEquals(
            "/marketplace/settings/product/screens/editor-description.json",
            url("description"),
        )

    @Test
    fun `quantity`() =
        assertEndpointEquals("/marketplace/settings/product/screens/editor-quantity.json", url("quantity"))

    @Test
    fun `price`() =
        assertEndpointEquals("/marketplace/settings/product/screens/editor-price.json", url("price"))

    @Test
    fun submit() {
        // WHEN
        val request = SubmitAttributeRequest(value = "Hello")
        val response = rest.postForEntity(url("title", "/submit"), request, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/..", action.url)

        verify(marketplaceManagerApi).updateProductAttribute(
            UpdateProductAttributeListRequest(
                productId = product.id,
                attributes = listOf(
                    ProductAttribute("title", request.value),
                ),
            ),
        )
    }
}
