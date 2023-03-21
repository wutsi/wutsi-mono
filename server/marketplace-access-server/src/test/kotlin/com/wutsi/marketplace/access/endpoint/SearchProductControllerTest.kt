package com.wutsi.marketplace.access.endpoint

import com.wutsi.enums.ProductSort
import com.wutsi.enums.ProductStatus
import com.wutsi.enums.ProductType
import com.wutsi.marketplace.access.dto.SearchProductRequest
import com.wutsi.marketplace.access.dto.SearchProductResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SearchProductController.sql"])
class SearchProductControllerTest {
    @LocalServerPort
    val port: Int = 0

    private val rest = RestTemplate()

    @Test
    fun published() {
        // GIVEN
        val request = SearchProductRequest(
            status = ProductStatus.PUBLISHED.name,
        )
        val response = rest.postForEntity(url(), request, SearchProductResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val productIds = response.body!!.products.map { it.id }
        assertEquals(3, productIds.size)
        assertEquals(listOf(101L, 102L, 100L), productIds)
    }

    @Test
    fun byCategoryIds() {
        // GIVEN
        val request = SearchProductRequest(
            categoryIds = listOf(1120L),
            sortBy = ProductSort.PRICE_ASC.name,
        )
        val response = rest.postForEntity(url(), request, SearchProductResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val productIds = response.body!!.products.map { it.id }
        assertEquals(2, productIds.size)
        assertEquals(listOf(102L, 103L), productIds)
    }

    @Test
    fun byProductIds() {
        // GIVEN
        val request = SearchProductRequest(
            productIds = listOf(100L, 101L, 199L, 201L),
            sortBy = ProductSort.PRICE_DESC.name,
        )
        val response = rest.postForEntity(url(), request, SearchProductResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val productIds = response.body!!.products.map { it.id }
        assertEquals(2, productIds.size)
        assertEquals(listOf(100L, 101L), productIds)
    }

    @Test
    fun byStoreId() {
        // GIVEN
        val request = SearchProductRequest(
            storeId = 1L,
        )
        val response = rest.postForEntity(url(), request, SearchProductResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val productIds = response.body!!.products.map { it.id }
        assertEquals(4, productIds.size)
    }

    @Test
    fun byType() {
        // GIVEN
        val request = SearchProductRequest(
            types = listOf(ProductType.EVENT.name, ProductType.DIGITAL_DOWNLOAD.name),
        )
        val response = rest.postForEntity(url(), request, SearchProductResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val productIds = response.body!!.products.map { it.id }.sorted()
        assertEquals(3, productIds.size)
        assertEquals(listOf(100L, 101L, 102L), productIds)
    }

    private fun url() = "http://localhost:$port/v1/products/search"
}
