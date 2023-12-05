package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dao.StoreRepository
import com.wutsi.blog.product.dto.CreateStoreCommand
import com.wutsi.blog.product.dto.CreateStoreResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/product/CreateStoreCommand.sql"])
class CreateStoreCommandExecutorTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: StoreRepository

    @Test
    fun create() {
        val request = CreateStoreCommand(
            userId = 100,
            feedUrl = "https://www.goo.com"
        )
        val result = rest.postForEntity("/v1/stores", request, CreateStoreResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val id = result.body!!.storeId
        val store = dao.findById(id).get()
        assertEquals("XAF", store.currency)
        assertEquals(100L, store.userId)
        assertEquals(0L, store.productCount)
        assertEquals(0L, store.orderCount)
        assertEquals(0L, store.totalSales)
        assertEquals("https://www.goo.com", store.feedUrl)
    }

    @Test
    fun alreadyCreated() {
        val request = CreateStoreCommand(
            userId = 200,
            feedUrl = "https://www.goo.com"
        )
        val result = rest.postForEntity("/v1/stores", request, CreateStoreResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val id = result.body!!.storeId
        assertEquals("200", id)

        val store = dao.findById(id).get()
        assertEquals("XAF", store.currency)
        assertEquals(200L, store.userId)
        assertEquals(11L, store.productCount)
        assertEquals(111L, store.orderCount)
        assertEquals(111000L, store.totalSales)
        assertEquals("https://www.goo.com/200.csv", store.feedUrl)
    }
}
