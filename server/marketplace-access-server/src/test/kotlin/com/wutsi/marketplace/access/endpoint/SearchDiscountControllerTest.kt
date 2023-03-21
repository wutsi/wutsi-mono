package com.wutsi.marketplace.access.endpoint

import com.wutsi.enums.DiscountType
import com.wutsi.marketplace.access.dto.SearchDiscountRequest
import com.wutsi.marketplace.access.dto.SearchDiscountResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import java.time.LocalDate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SearchDiscountController.sql"])
class SearchDiscountControllerTest {
    @LocalServerPort
    val port: Int = 0

    private val rest = RestTemplate()

    @Test
    public fun searchByStore() {
        val request = SearchDiscountRequest(
            storeId = 2L,
        )
        val response = rest.postForEntity(url(), request, SearchDiscountResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val discounts = response.body!!.discounts
        assertEquals(listOf(200L), discounts.map { it.id })
    }

    @Test
    public fun searchByDate() {
        val request = SearchDiscountRequest(
            storeId = 1L,
            date = LocalDate.of(2020, 1, 6),
        )
        val response = rest.postForEntity(url(), request, SearchDiscountResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val discounts = response.body!!.discounts
        assertEquals(listOf(100L, 101L), discounts.map { it.id })
    }

    @Test
    public fun searchByProduct() {
        val request = SearchDiscountRequest(
            storeId = 1L,
            productIds = listOf(100),
        )
        val response = rest.postForEntity(url(), request, SearchDiscountResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val discounts = response.body!!.discounts
        assertEquals(listOf(100L, 101L, 102L, 110L), discounts.map { it.id })
    }

    @Test
    public fun searchByType() {
        val request = SearchDiscountRequest(
            storeId = 3L,
            type = DiscountType.COUPON.name,
        )
        val response = rest.postForEntity(url(), request, SearchDiscountResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val discounts = response.body!!.discounts
        assertEquals(listOf(300L), discounts.map { it.id })
    }

    private fun url() = "http://localhost:$port/v1/discounts/search"
}
