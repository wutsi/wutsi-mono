package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.enums.DiscountType
import com.wutsi.marketplace.access.dto.SearchDiscountResponse
import com.wutsi.marketplace.manager.Fixtures
import com.wutsi.marketplace.manager.dto.SearchDiscountRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.time.LocalDate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SearchDiscountControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    public val port: Int = 0

    @Test
    public fun search() {
        val discounts = listOf(
            Fixtures.createDiscountSummary(100, rate = 10),
            Fixtures.createDiscountSummary(200, rate = 20),
            Fixtures.createDiscountSummary(300, rate = 30),
        )
        doReturn(SearchDiscountResponse(discounts)).whenever(marketplaceAccessApi).searchDiscount(any())

        // WHEN
        val request = SearchDiscountRequest(
            productIds = listOf(1, 2, 3),
            discountIds = listOf(100, 200, 300),
            date = LocalDate.now(),
            storeId = 111,
            limit = 300,
            offset = 1,
            type = DiscountType.COUPON.name,
        )
        val response = rest.postForEntity(url(), request, SearchDiscountResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val result = response.body!!.discounts
        assertEquals(discounts.size, result.size)

        verify(marketplaceAccessApi).searchDiscount(
            com.wutsi.marketplace.access.dto.SearchDiscountRequest(
                productIds = request.productIds,
                discountIds = request.discountIds,
                date = request.date,
                storeId = request.storeId,
                limit = request.limit,
                offset = request.offset,
                type = request.type,
            ),
        )
    }

    private fun url() = "http://localhost:$port/v1/discounts/search"
}
