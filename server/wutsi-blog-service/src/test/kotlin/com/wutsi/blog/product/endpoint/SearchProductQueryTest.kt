package com.wutsi.blog.product.endpoint

import com.wutsi.blog.SortOrder
import com.wutsi.blog.product.dto.ProductSortStrategy
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.dto.SearchProductResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/product/SearchProductQuery.sql"])
class SearchProductQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun byStatus() {
        val request = SearchProductRequest(
            status = ProductStatus.PUBLISHED
        )
        val result = rest.postForEntity("/v1/products/queries/search", request, SearchProductResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val products = result.body!!.products

        assertEquals(3, products.size)
        assertTrue(products.map { it.id }.containsAll(listOf(101L, 102L, 201L)))
    }

    @Test
    fun byStoreId() {
        val request = SearchProductRequest(
            storeIds = listOf("1")
        )
        val result = rest.postForEntity("/v1/products/queries/search", request, SearchProductResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val products = result.body!!.products

        assertEquals(3, products.size)
        assertTrue(products.map { it.id }.containsAll(listOf(101L, 102L, 103L)))
    }

    @Test
    fun byExternalId() {
        val request = SearchProductRequest(
            externalIds = listOf("101", "102")
        )
        val result = rest.postForEntity("/v1/products/queries/search", request, SearchProductResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val products = result.body!!.products

        assertEquals(2, products.size)
        assertTrue(products.map { it.id }.containsAll(listOf(101L, 102L)))
    }

    @Test
    fun sortByPrice() {
        val request = SearchProductRequest(
            storeIds = listOf("1"),
            sortBy = ProductSortStrategy.PRICE,
            sortOrder = SortOrder.DESCENDING
        )
        val result = rest.postForEntity("/v1/products/queries/search", request, SearchProductResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val products = result.body!!.products

        assertEquals(3, products.size)
        assertEquals(listOf(102L, 101L, 103L), products.map { it.id })
    }
}
