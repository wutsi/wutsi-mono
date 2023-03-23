package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.enums.ProductStatus
import com.wutsi.enums.ProductType
import com.wutsi.marketplace.manager.Fixtures
import com.wutsi.marketplace.manager.dto.SearchProductRequest
import com.wutsi.marketplace.manager.dto.SearchProductResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SearchProductControllerTest : AbstractControllerTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port/v1/products/search"

    val request = SearchProductRequest(
        productIds = listOf(100L, 200L),
        categoryIds = listOf(1L, 2L, 3L),
        storeId = 111L,
        limit = 100,
        offset = 0,
        status = ProductStatus.PUBLISHED.name,
        sortBy = "RECOMMENDED",
        types = listOf(ProductType.PHYSICAL_PRODUCT.name, ProductType.EVENT.name),
    )

    @Test
    public fun invoke() {
        // GIVEN
        val products = listOf(
            Fixtures.createProductSummary(id = 1),
            Fixtures.createProductSummary(id = 2),
            Fixtures.createProductSummary(id = 3),
        )
        doReturn(com.wutsi.marketplace.access.dto.SearchProductResponse(products)).whenever(marketplaceAccessApi)
            .searchProduct(
                any(),
            )

        // WHEN
        val response = rest.postForEntity(url(), request, SearchProductResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(marketplaceAccessApi).searchProduct(
            com.wutsi.marketplace.access.dto.SearchProductRequest(
                productIds = request!!.productIds,
                categoryIds = request!!.categoryIds,
                storeId = request!!.storeId,
                limit = request!!.limit,
                offset = request!!.offset,
                status = request!!.status,
                sortBy = request!!.sortBy,
                types = request!!.types,
            ),
        )

        assertEquals(products.size, response.body!!.products.size)

        verify(eventStream, never()).publish(any(), any())
    }
}
