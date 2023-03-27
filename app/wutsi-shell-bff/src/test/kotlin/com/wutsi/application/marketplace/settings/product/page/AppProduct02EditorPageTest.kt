package com.wutsi.application.marketplace.settings.product.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import com.wutsi.application.marketplace.settings.product.dto.SubmitProductRequest
import com.wutsi.application.marketplace.settings.product.entity.PictureEntity
import com.wutsi.enums.ProductType
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.marketplace.manager.dto.CreateProductRequest
import com.wutsi.marketplace.manager.dto.CreateProductResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class AppProduct02EditorPageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val entity = PictureEntity(
        url = "http://www.google.ca/1.png",
        type = ProductType.EVENT,
    )

    private fun url(action: String = "") =
        "http://localhost:$port${Page.getSettingsProductAddUrl()}/pages/editor$action"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(entity).whenever(cache).get(any(), any<Class<*>>())
    }

    @Test
    fun index() = assertEndpointEquals("/marketplace/settings/product/pages/picture-01.json", url())

    @Test
    fun submit() {
        // GIVEN
        doReturn(CreateProductResponse(123)).whenever(marketplaceManagerApi).createProduct(any())

        // WHEN
        val request = SubmitProductRequest(
            title = "Yo man",
            summary = "This looks awesome",
            price = 15000,
            quantity = "10",
        )
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        verify(marketplaceManagerApi).createProduct(
            request = CreateProductRequest(
                pictureUrl = entity.url,
                title = request.title,
                price = request.price,
                quantity = request.quantity.toInt(),
                summary = request.summary,
                type = entity.type.name,
            ),
        )

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0${Page.getSettingsProductUrl()}", action.url)
        assertEquals(mapOf("id" to "123"), action.parameters)
        assertEquals(true, action.replacement)
    }
}
