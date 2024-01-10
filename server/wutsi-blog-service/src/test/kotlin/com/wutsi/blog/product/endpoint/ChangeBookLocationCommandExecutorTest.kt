package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dao.BookRepository
import com.wutsi.blog.product.dto.ChangeBookLocationCommand
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/product/ChangeBookLocationCommandExecutor.sql"])
class ChangeBookLocationCommandExecutorTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: BookRepository

    @Test
    fun execute() {
        val request = ChangeBookLocationCommand(
            bookId = 100,
            location = "a",
            readPercentage = 90,
        )

        val response = rest.postForEntity("/v1/books/commands/change-location", request, Any::class.java)
        assertEquals(200, response.statusCode.value())

        val book = dao.findById(request.bookId).get()

        assertEquals(request.location, book.location)
        assertEquals(request.readPercentage, book.readPercentage)
    }
}
