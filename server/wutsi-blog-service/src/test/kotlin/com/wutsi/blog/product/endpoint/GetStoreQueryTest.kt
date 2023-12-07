package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.GetStoreResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/product/GetStoreQuery.sql"])
class GetStoreQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun get() {
        val result = rest.getForEntity("/v1/stores/1", GetStoreResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val story = result.body!!.store
        assertEquals("XAF", story.currency)
        assertEquals(100L, story.userId)
        assertEquals(11L, story.productCount)
        assertEquals(111L, story.orderCount)
        assertEquals(111000L, story.totalSales)
    }

    @Test
    fun notFound() {
        val result = rest.getForEntity("/v1/stores/999", GetStoreResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
    }
}
