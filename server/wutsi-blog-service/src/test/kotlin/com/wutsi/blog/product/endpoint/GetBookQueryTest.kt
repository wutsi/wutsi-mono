package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.GetBookResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/product/GetBookQuery.sql"])
class GetBookQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun searchByUser() {
        val result = rest.getForEntity("/v1/books/100", GetBookResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val book = result.body!!.book

        assertEquals(100, book.userId)
        assertEquals("book-100.html", book.location)
        assertEquals("100", book.transactionId)
        assertNull(book.expiryDate)

        assertEquals(100, book.product.id)
        assertEquals("product 100", book.product.title)
        assertEquals("XAF", book.product.currency)
        assertEquals(1000L, book.product.price)
    }
}
