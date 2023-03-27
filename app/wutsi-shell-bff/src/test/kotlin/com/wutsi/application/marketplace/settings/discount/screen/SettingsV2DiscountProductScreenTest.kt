package com.wutsi.application.marketplace.settings.discount.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.marketplace.manager.dto.GetDiscountResponse
import com.wutsi.marketplace.manager.dto.SearchProductResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class SettingsV2DiscountProductScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url(id: Long, action: String = "") =
        "http://localhost:$port${Page.getSettingsDiscountProductUrl()}$action?id=$id"

    @Test
    fun products() {
        // GIVEN
        val discount = Fixtures.createDiscount(
            id = 111L,
            allProduct = false,
            productIds = listOf(100, 200, 300),
        )
        doReturn(GetDiscountResponse(discount)).whenever(marketplaceManagerApi).getDiscount(any())

        val products = listOf(
            Fixtures.createProductSummary(100),
            Fixtures.createProductSummary(101),
            Fixtures.createProductSummary(200),
            Fixtures.createProductSummary(201),
            Fixtures.createProductSummary(300),
        )
        doReturn(SearchProductResponse(products)).whenever(marketplaceManagerApi).searchProduct(any())

        // WHEN
        assertEndpointEquals("/marketplace/settings/discount/screens/products.json", url(100))
    }

    @Test
    fun remove() {
        val url =
            "http://localhost:$port${Page.getSettingsDiscountProductUrl()}/toggle?discount-id=100&product-id=101&value=true"
        rest.postForEntity(url, null, Any::class.java)

        verify(marketplaceManagerApi).removeDiscountProduct(100, 101)
    }

    @Test
    fun add() {
        val url =
            "http://localhost:$port${Page.getSettingsDiscountProductUrl()}/toggle?discount-id=100&product-id=101&value=false"
        rest.postForEntity(url, null, Any::class.java)

        verify(marketplaceManagerApi).addDiscountProduct(100, 101)
    }
}
