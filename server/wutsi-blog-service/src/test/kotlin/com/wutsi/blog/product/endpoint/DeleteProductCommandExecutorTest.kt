package com.wutsi.blog.product.endpoint

import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.product.dao.ProductRepository
import com.wutsi.blog.product.dto.DeleteProductCommand
import com.wutsi.event.store.EventStore
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/product/DeleteProductCommand.sql"])
class DeleteProductCommandExecutorTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: ProductRepository

    @Autowired
    private lateinit var eventStore: EventStore

    @Test
    fun delete() {
        val request = DeleteProductCommand(
            productId = 1L,
        )
        val result = rest.postForEntity("/v1/products/commands/delete", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val product = dao.findById(request.productId).get()
        assertTrue(product.deleted)
        assertNotNull(product.deletedDateTime)

        val events = eventStore.events(
            streamId = StreamId.PRODUCT,
            type = EventType.PRODUCT_DELETED_EVENT,
            entityId = request.productId.toString()
        )
        assertTrue(events.isNotEmpty())
    }

    @Test
    fun alreadyDeleted() {
        val request = DeleteProductCommand(
            productId = 9L,
        )
        val result = rest.postForEntity("/v1/products/commands/delete", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val product = dao.findById(request.productId).get()
        assertTrue(product.deleted)

        val events = eventStore.events(
            streamId = StreamId.PRODUCT,
            type = EventType.PRODUCT_DELETED_EVENT,
            entityId = request.productId.toString()
        )
        assertTrue(events.isEmpty())
    }

    @Test
    fun notFound() {
        val request = DeleteProductCommand(
            productId = 9999L,
        )
        val result = rest.postForEntity("/v1/products/commands/delete", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val events = eventStore.events(
            streamId = StreamId.PRODUCT,
            type = EventType.PRODUCT_DELETED_EVENT,
            entityId = request.productId.toString()
        )
        assertTrue(events.isEmpty())
    }
}
