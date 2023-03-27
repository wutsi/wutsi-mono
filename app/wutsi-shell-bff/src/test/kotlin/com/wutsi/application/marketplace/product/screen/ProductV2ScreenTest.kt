package com.wutsi.application.marketplace.product.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.enums.ProductType
import com.wutsi.marketplace.manager.dto.GetOfferResponse
import com.wutsi.marketplace.manager.dto.GetStoreResponse
import com.wutsi.membership.manager.dto.GetMemberResponse
import com.wutsi.regulation.RegulationEngine
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort

internal class ProductV2ScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @Autowired
    private lateinit var regulationEngine: RegulationEngine

    val storeId = 1111L
    val accountId = 222L
    val productId = 444L

    private fun url() = "http://localhost:$port${Page.getProductUrl()}?id=$productId"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        val store = Fixtures.createStore(storeId, accountId)
        doReturn(GetStoreResponse(store)).whenever(marketplaceManagerApi).getStore(any())

        val account = Fixtures.createMember(
            id = accountId,
            storeId = storeId,
        )
        doReturn(GetMemberResponse(account)).whenever(membershipManagerApi).getMember(anyOrNull())
    }

    @Test
    fun physical() {
        val offer = Fixtures.createOffer(
            product = Fixtures.createProduct(
                id = productId,
                storeId = storeId,
                pictures = listOf(
                    Fixtures.createPictureSummary(id = 1),
                    Fixtures.createPictureSummary(id = 2),
                    Fixtures.createPictureSummary(id = 3),
                    Fixtures.createPictureSummary(id = 4),
                ),
            ),
            price = Fixtures.createOfferPrice(productId, 1, 3500, 3000, 5000),
        )
        doReturn(GetOfferResponse(offer)).whenever(marketplaceManagerApi).getOffer(any())

        assertEndpointEquals("/marketplace/product/screens/product-physical.json", url())
    }

    @Test
    fun event() {
        val offer = Fixtures.createOffer(
            product = Fixtures.createProduct(
                id = productId,
                storeId = storeId,
                pictures = listOf(
                    Fixtures.createPictureSummary(id = 1),
                    Fixtures.createPictureSummary(id = 2),
                    Fixtures.createPictureSummary(id = 3),
                    Fixtures.createPictureSummary(id = 4),
                ),
                type = ProductType.EVENT,
                event = Fixtures.createEvent(
                    meetingProvider = Fixtures.createMeetingProviderSummary(),
                ),
                description = "Yo man",
            ),
        )
        doReturn(GetOfferResponse(offer)).whenever(marketplaceManagerApi).getOffer(any())

        assertEndpointEquals("/marketplace/product/screens/product-event.json", url())
    }

    @Test
    fun lowStock() {
        val offer = Fixtures.createOffer(
            product = Fixtures.createProduct(
                id = productId,
                storeId = storeId,
                pictures = listOf(
                    Fixtures.createPictureSummary(id = 1),
                ),
                type = ProductType.PHYSICAL_PRODUCT,
                description = "Yo man",
                quantity = regulationEngine.lowStockThreshold() - 1,
            ),
        )
        doReturn(GetOfferResponse(offer)).whenever(marketplaceManagerApi).getOffer(any())

        assertEndpointEquals("/marketplace/product/screens/product-low-stock.json", url())
    }

    @Test
    fun outOfStock() {
        val offer = Fixtures.createOffer(
            product = Fixtures.createProduct(
                id = productId,
                storeId = storeId,
                pictures = listOf(
                    Fixtures.createPictureSummary(id = 1),
                ),
                type = ProductType.PHYSICAL_PRODUCT,
                description = "Yo man",
                quantity = 0,
            ),
        )
        doReturn(GetOfferResponse(offer)).whenever(marketplaceManagerApi).getOffer(any())

        assertEndpointEquals("/marketplace/product/screens/product-out-of-stock.json", url())
    }
}
