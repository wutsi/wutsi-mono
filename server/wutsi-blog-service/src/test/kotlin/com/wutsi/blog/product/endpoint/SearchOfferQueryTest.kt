package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.product.dto.SearchOfferRequest
import com.wutsi.blog.product.dto.SearchOfferResponse
import com.wutsi.blog.util.DateUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/product/SearchOfferQuery.sql"])
class SearchOfferQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun search() {
        val request = SearchOfferRequest(
            userId = 300,
            productIds = listOf(101, 201, 110)
        )
        val result = rest.postForEntity("/v1/offers/queries/search", request, SearchOfferResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val offers = result.body!!.offers
        val fmt = SimpleDateFormat("yyyy/MM/dd")

        assertEquals(3, offers.size)

        assertEquals(request.productIds[0], offers[0].productId)
        assertEquals(1000, offers[0].referencePrice)
        assertEquals(900, offers[0].price)
        assertEquals(100, offers[0].savingAmount)
        assertEquals(10, offers[0].savingPercentage)
        assertEquals(10, offers[0].discount?.percentage)
        assertEquals(DiscountType.SUBSCRIBER, offers[0].discount?.type)
        assertNull(offers[0].discount?.expiryDate)
        assertNull(offers[0].discount?.couponId)

        assertEquals(request.productIds[1], offers[1].productId)
        assertEquals(1500, offers[1].referencePrice)
        assertEquals(1200, offers[1].price)
        assertEquals(300, offers[1].savingAmount)
        assertEquals(20, offers[1].savingPercentage)
        assertEquals(20, offers[1].discount?.percentage)
        assertEquals(DiscountType.FIRST_PURCHASE, offers[1].discount?.type)
        assertNull(offers[1].discount?.expiryDate)
        assertNull(offers[1].discount?.couponId)

        assertEquals(request.productIds[2], offers[2].productId)
        assertEquals(5000, offers[2].referencePrice)
        assertEquals(3000, offers[2].price)
        assertEquals(2000, offers[2].savingAmount)
        assertEquals(40, offers[2].savingPercentage)
        assertEquals(40, offers[2].discount?.percentage)
        assertEquals(DiscountType.COUPON, offers[2].discount?.type)
        assertEquals(fmt.format(DateUtils.addDays(Date(), 10)), fmt.format(offers[2].discount?.expiryDate))
        assertEquals(1L, offers[2].discount?.couponId)
    }

    @Test
    fun searchExpiredCoupon() {
        val request = SearchOfferRequest(
            userId = 300,
            productIds = listOf(310)
        )
        val result = rest.postForEntity("/v1/offers/queries/search", request, SearchOfferResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val offers = result.body!!.offers

        assertEquals(1, offers.size)

        assertEquals(request.productIds[0], offers[0].productId)
        assertEquals(5000, offers[0].referencePrice)
        assertEquals(5000, offers[0].price)
        assertEquals(0, offers[0].savingAmount)
        assertEquals(0, offers[0].savingPercentage)
        assertNull(offers[0].discount)
    }

    @Test
    fun searchAlreadyUsed() {
        val request = SearchOfferRequest(
            userId = 300,
            productIds = listOf(320)
        )
        val result = rest.postForEntity("/v1/offers/queries/search", request, SearchOfferResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val offers = result.body!!.offers

        assertEquals(1, offers.size)

        assertEquals(request.productIds[0], offers[0].productId)
        assertEquals(5000, offers[0].referencePrice)
        assertEquals(5000, offers[0].price)
        assertEquals(0, offers[0].savingAmount)
        assertEquals(0, offers[0].savingPercentage)
        assertNull(offers[0].discount)
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
