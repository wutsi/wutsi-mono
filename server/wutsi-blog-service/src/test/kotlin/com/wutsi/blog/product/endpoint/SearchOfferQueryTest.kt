package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.product.dto.SearchOfferRequest
import com.wutsi.blog.product.dto.SearchOfferResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/product/SearchOfferQuery.sql"])
class SearchOfferQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun search() {
        val request = SearchOfferRequest(
            userId = 300,
            productIds = listOf(101, 201)
        )
        val result = rest.postForEntity("/v1/offers/queries/search", request, SearchOfferResponse::class.java)

        kotlin.test.assertEquals(HttpStatus.OK, result.statusCode)
        val offers = result.body!!.offers

        assertEquals(2, offers.size)

        assertEquals(1000, offers[0].referencePrice)
        assertEquals(900, offers[0].price)
        assertEquals(100, offers[0].savingAmount)
        assertEquals(10, offers[0].savingPercentage)
        assertEquals(10, offers[0].discount?.percentage)
        assertEquals(DiscountType.SUBSCRIBER, offers[0].discount?.type)
        assertNull(offers[0].discount?.expiryDate)

        assertEquals(1500, offers[1].referencePrice)
        assertEquals(1200, offers[1].price)
        assertEquals(300, offers[1].savingAmount)
        assertEquals(20, offers[1].savingPercentage)
        assertEquals(20, offers[1].discount?.percentage)
        assertEquals(DiscountType.FIRST_PURCHASE, offers[1].discount?.type)
        assertNull(offers[1].discount?.expiryDate)
    }

    @Test
    fun anonymous() {
        val request = SearchOfferRequest(
            userId = null,
            productIds = listOf(101, 201)
        )
        val result = rest.postForEntity("/v1/offers/queries/search", request, SearchOfferResponse::class.java)

        kotlin.test.assertEquals(HttpStatus.OK, result.statusCode)
        val offers = result.body!!.offers

        assertEquals(2, offers.size)

        assertEquals(1000, offers[0].referencePrice)
        assertEquals(1000, offers[0].price)
        assertEquals(0, offers[0].savingAmount)
        assertEquals(0, offers[0].savingPercentage)
        assertNull(offers[0].discount)

        assertEquals(1500, offers[1].referencePrice)
        assertEquals(1500, offers[1].price)
        assertEquals(0, offers[1].savingAmount)
        assertEquals(0, offers[1].savingPercentage)
        assertNull(offers[0].discount)
    }
}
