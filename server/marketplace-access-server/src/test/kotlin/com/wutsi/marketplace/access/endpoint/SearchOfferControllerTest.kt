package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.dto.SearchOfferRequest
import com.wutsi.marketplace.access.dto.SearchOfferResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SearchOfferController.sql"])
public class SearchOfferControllerTest : AbstractLanguageAwareControllerTest() {
    @LocalServerPort
    public val port: Int = 0

    @Test
    fun `offer without discount`() {
        // WHEN
        val request = SearchOfferRequest(
            storeId = 3L,
            productIds = listOf(300L),
        )
        val response = rest.postForEntity(url(), request, SearchOfferResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val offers = response.body!!.offers
        assertEquals(1, offers.size)
        assertEquals(300L, offers[0].product.id)
        assertEquals(300L, offers[0].price.productId)
        assertEquals(300000L, offers[0].price.price)
        assertNull(offers[0].price.referencePrice)
        assertNull(offers[0].price.discountId)
        assertEquals(0L, offers[0].price.savings)
        assertEquals(0, offers[0].price.savingsPercentage)
        assertNull(offers[0].price.expires)
    }

    @Test
    fun `product with discounts applied to all products`() {
        // WHEN
        val request = SearchOfferRequest(
            storeId = 1L,
            productIds = listOf(100L),
        )
        val response = rest.postForEntity(url(), request, SearchOfferResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val offers = response.body!!.offers
        assertEquals(1, offers.size)
        assertEquals(100L, offers[0].product.id)
        assertEquals(100L, offers[0].price.productId)
        assertEquals(112500L, offers[0].price.price)
        assertEquals(150000L, offers[0].price.referencePrice)
        assertEquals(101L, offers[0].price.discountId)
        assertEquals(37500L, offers[0].price.savings)
        assertEquals(25, offers[0].price.savingsPercentage)
        assertNotNull(offers[0].price.expires)
    }

    @Test
    fun `product with discounts applied to specific products`() {
        // WHEN
        val request = SearchOfferRequest(
            storeId = 2L,
            productIds = listOf(200L),
        )
        val response = rest.postForEntity(url(), request, SearchOfferResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val offers = response.body!!.offers
        assertEquals(1, offers.size)
        assertEquals(200L, offers[0].product.id)
        assertEquals(200L, offers[0].price.productId)
        assertEquals(1600L, offers[0].price.price)
        assertEquals(2000L, offers[0].price.referencePrice)
        assertEquals(200L, offers[0].price.discountId)
        assertEquals(400L, offers[0].price.savings)
        assertEquals(20, offers[0].price.savingsPercentage)
        assertNotNull(offers[0].price.expires)
    }

    @Test
    fun `search multiple products`() {
        // WHEN
        val request = SearchOfferRequest(
            storeId = 5L,
        )
        val response = rest.postForEntity(url(), request, SearchOfferResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val offers = response.body!!.offers
        assertEquals(3, offers.size)

        assertEquals(500L, offers[0].price.productId)
        assertEquals(1600L, offers[0].price.price)
        assertEquals(2000L, offers[0].price.referencePrice)
        assertEquals(500L, offers[0].price.discountId)
        assertEquals(400L, offers[0].price.savings)
        assertEquals(20, offers[0].price.savingsPercentage)
        assertNotNull(offers[0].price.expires)

        assertEquals(501L, offers[1].price.productId)
        assertEquals(1500L, offers[1].price.price)
        assertEquals(2000L, offers[1].price.referencePrice)
        assertEquals(501L, offers[1].price.discountId)
        assertEquals(500L, offers[1].price.savings)
        assertEquals(25, offers[1].price.savingsPercentage)
        assertNotNull(offers[1].price.expires)

        assertEquals(502L, offers[2].price.productId)
        assertEquals(240000L, offers[2].price.price)
        assertEquals(300000L, offers[2].price.referencePrice)
        assertEquals(500L, offers[2].price.discountId)
        assertEquals(60000L, offers[2].price.savings)
        assertEquals(20, offers[2].price.savingsPercentage)
        assertNotNull(offers[2].price.expires)
    }

    private fun url() = "http://localhost:$port/v1/offers/search"
}
