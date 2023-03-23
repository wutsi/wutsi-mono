package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.enums.ProductSort
import com.wutsi.enums.ProductType
import com.wutsi.marketplace.manager.Fixtures
import com.wutsi.marketplace.manager.dto.SearchOfferRequest
import com.wutsi.marketplace.manager.dto.SearchOfferResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SearchOfferControllerTest : AbstractControllerTest() {
    @LocalServerPort
    public val port: Int = 0

    @Test
    public fun invoke() {
        val offers = listOf(
            Fixtures.createOfferSummary(1L),
            Fixtures.createOfferSummary(2L),
            Fixtures.createOfferSummary(3L),
        )
        doReturn(com.wutsi.marketplace.access.dto.SearchOfferResponse(offers)).whenever(marketplaceAccessApi)
            .searchOffer(any())

        val request = SearchOfferRequest(
            storeId = 1L,
            productIds = listOf(1L, 2L),
            limit = 100,
            offset = 0,
            sortBy = ProductSort.RECOMMENDED.name,
            types = listOf(ProductType.PHYSICAL_PRODUCT.name, ProductType.EVENT.name),
        )
        val response = rest.postForEntity(url(), request, SearchOfferResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        assertEquals(offers.size, response.body!!.offers.size)

        verify(marketplaceAccessApi).searchOffer(
            com.wutsi.marketplace.access.dto.SearchOfferRequest(
                storeId = request.storeId,
                productIds = request.productIds,
                limit = request.limit,
                offset = request.offset,
                sortBy = request.sortBy,
                types = request.types,
            ),
        )
    }

    private fun url() = "http://localhost:$port/v1/offers/search"
}
