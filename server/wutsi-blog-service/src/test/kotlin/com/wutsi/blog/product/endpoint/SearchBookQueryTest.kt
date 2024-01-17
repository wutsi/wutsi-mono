package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.SearchBookRequest
import com.wutsi.blog.product.dto.SearchBookResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/product/SearchBookQuery.sql"])
class SearchBookQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun searchByUser() {
        val request = SearchBookRequest(
            userId = 100,
        )
        val result = rest.postForEntity("/v1/books/queries/search", request, SearchBookResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val books = result.body!!.books

        assertEquals(2, books.size)

        assertEquals(100, books[0].userId)
        assertEquals(100, books[0].product.id)
        assertEquals("product 100", books[0].product.title)

        assertEquals(100, books[1].userId)
        assertEquals("product 101", books[1].product.title)
    }

    @Test
    fun searchByUserAndProduct() {
        val request = SearchBookRequest(
            userId = 100,
            productIds = listOf(101)
        )
        val result = rest.postForEntity("/v1/books/queries/search", request, SearchBookResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val books = result.body!!.books

        assertEquals(1, books.size)
        assertEquals(101, books[0].id)
    }

    @Test
    fun searchByTransactionId() {
        val request = SearchBookRequest(
            transactionId = "200"
        )
        val result = rest.postForEntity("/v1/books/queries/search", request, SearchBookResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val books = result.body!!.books

        assertEquals(1, books.size)
        assertEquals(200, books[0].id)
    }
}
