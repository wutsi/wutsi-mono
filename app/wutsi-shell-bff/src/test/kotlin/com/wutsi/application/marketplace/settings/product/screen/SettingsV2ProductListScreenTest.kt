package com.wutsi.application.marketplace.settings.product.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.marketplace.manager.dto.ProductSummary
import com.wutsi.marketplace.manager.dto.SearchProductResponse
import com.wutsi.regulation.RegulationEngine
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort

internal class SettingsV2ProductListScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @Autowired
    private lateinit var regulationEngine: RegulationEngine

    private fun url() = "http://localhost:$port${Page.getSettingsProductListUrl()}"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        val products = listOf(
            Fixtures.createProductSummary(1, "Product1", "http://www.google.ca/1.png", published = true),
            Fixtures.createProductSummary(2, "Product 2", published = false),
        )
        doReturn(SearchProductResponse(products)).whenever(marketplaceManagerApi).searchProduct(any())
    }

    @Test
    fun index() = assertEndpointEquals("/marketplace/settings/product/screens/product-list.json", url())

    @Test
    fun limit() {
        val products = mutableListOf<ProductSummary>()
        while (products.size < regulationEngine.maxProducts()) {
            products.add(Fixtures.createProductSummary())
        }
        doReturn(SearchProductResponse(products)).whenever(marketplaceManagerApi).searchProduct(any())

        assertEndpointEquals("/marketplace/settings/product/screens/product-list-limit.json", url())
    }
}
